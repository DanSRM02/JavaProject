package com.oxi.software.utilities.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.key.private}")
    private String secretKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    // Generar un token JWT
    public String createToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey.trim());

        //Usuario
        String username = authentication.getPrincipal().toString();
        List<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(role -> role.startsWith("ROLE"))
                .collect(Collectors.toList());

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withClaim("role", roles)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24000000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }


    public DecodedJWT verifyToken(String token) {
      try{
          Algorithm algorithm = Algorithm.HMAC256(this.secretKey.trim());

          JWTVerifier verifier = JWT.require(algorithm)
                  .withIssuer(this.userGenerator)
                  .build();

          return verifier.verify(token);
      } catch (JWTVerificationException e){
          throw new JWTVerificationException("Token is not valid, not authorized" + e.getMessage());
      }
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Long extractDocument (DecodedJWT decodedJWT){
        return Long.valueOf(decodedJWT.getSubject());
    }
}
