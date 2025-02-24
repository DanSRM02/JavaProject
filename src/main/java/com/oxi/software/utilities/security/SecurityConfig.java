package com.oxi.software.utilities.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Cadena de filtros Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtTokenProvider jwtTokenProvider) throws Exception {
        return httpSecurity
                // Cross-Site Request Forgery desactivado
                .csrf(AbstractHttpConfigurer::disable)
                // Manejo de sesiones sin estilos -- STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Rutas habilitadas
                .authorizeHttpRequests(
                        http -> {
                            // Rutas públicas
                            http.requestMatchers("api/v1/oxi/auth/register").permitAll();
                            http.requestMatchers("api/v1/oxi/individual-type/all").permitAll();
                            http.requestMatchers("api/v1/oxi/document-type/all").permitAll();

                            // Rutas que no están definidas las deniega
                            http.anyRequest().denyAll();
                        })
                //Insertamos el nuevo filtro a la cadena
                .addFilterBefore(new JwtValidator(jwtTokenProvider), BasicAuthenticationFilter.class)
                .build();
    }


    // Password Encoder: Hashear contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
