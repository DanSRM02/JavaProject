package com.oxi.software.utilities.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;


import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;


public class JwtValidator extends OncePerRequestFilter {

    //Inyecciòn de dependencias mediante constructor
    //Debido a que no acepta el @Bean la clase padre OncePerRequestFilter
    private final JwtTokenProvider jwtTokenProvider;

    public JwtValidator(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //Creamos el filtro que va a validar nuestro token
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Obtenemos el token del encabezado
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Verificamos que sea nulo diferente a nulo
        //Si es nulo pasamos al siguiente filtro
        if (token != null) {
            //Formateamos el encabezado para obtener el token
            String jwtToken = token.substring(7);

            //Conseguimos el token decodificado
            DecodedJWT decodedJWT = jwtTokenProvider.verifyToken(jwtToken);

            //Accedemos al sujeto que esta en token
            String username = decodedJWT.getSubject();
            //Obtenemos las autorizaciones en un string separados por comas
            String authorities = decodedJWT.getClaim("authorities").asString();
            //Colección de los permisos ordenadas mediante esa función de AuthorityUtils
            Collection<? extends GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

            //Contexto Actual de Security
            SecurityContext context = SecurityContextHolder.getContext();
            //Luego seteamos el objeto con los permisos para autorizarlo
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
            //Finalmente lo insertamos dentro del contexto
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        }

        filterChain.doFilter(request, response);

    }
}
