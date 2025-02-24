package com.oxi.software.controller;

import com.oxi.software.business.UserBusiness;
import com.oxi.software.dto.UserDTO;
import com.oxi.software.dto.LoginRequest;
import com.oxi.software.utilities.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/oxi/user/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserBusiness userBusiness;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserBusiness userBusiness) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userBusiness = userBusiness;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
        logger.info("🔹 Intento de login para usuario: {}", loginRequest.getUsername());

        if (loginRequest.getUsername() == null || loginRequest.getUsername().isBlank() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            logger.error("❌ Username o password vacíos");
            return ResponseEntity.badRequest().body(Map.of("error", "Username y password no pueden estar vacíos"));
        }

        // Verificar si el usuario existe y autenticar
        UserDTO userDTO;
        try {
            userDTO = userBusiness.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        } catch (Exception e) {
            logger.warn("⚠️ Usuario no encontrado o credenciales incorrectas: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado o credenciales incorrectas"));
        }

        // Intentar autenticar con Spring Security
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar token JWT
            String token = jwtTokenProvider.generateToken(authentication);
            logger.info("✅ Usuario autenticado correctamente: {}", loginRequest.getUsername());

            return ResponseEntity.ok(Map.of("token", token, "message", "Autenticación exitosa"));
        } catch (BadCredentialsException e) {
            logger.error("❌ Credenciales inválidas para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales incorrectas"));
        } catch (Exception e) {
            logger.error("❌ Error de autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno en el login"));
        }
    }
}
