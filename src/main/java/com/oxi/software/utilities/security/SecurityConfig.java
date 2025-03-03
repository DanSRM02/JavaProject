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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


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
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> corsConfigurationSource())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        http -> {
                            // Rutas públicas
                            http.requestMatchers("/api/v1/oxi/auth/register").permitAll();
                            http.requestMatchers("/api/v1/oxi/auth/login").permitAll();
                            http.requestMatchers("/api/v1/oxi/individual-type/all").permitAll();
                            http.requestMatchers("/api/v1/oxi/document-type/all").permitAll();

                            // Rutas privadas - productos
                            http.requestMatchers("/api/v1/oxi/product/all").authenticated();
                            http.requestMatchers("/api/v1/oxi/product-variant/add").authenticated();
//                            http.requestMatchers("/api/v1/oxi/product-variant/delete/{id}").hasRole("ROLE_GERENTE");
                            http.requestMatchers("/api/v1/oxi/product-variant/update/{id}").authenticated();
                            http.requestMatchers("/api/v1/oxi/product-variant/find").authenticated();

                            // Ruta privada - unidades
                            http.requestMatchers("/api/v1/oxi/unit/all").authenticated();
                            http.requestMatchers("/api/v1/oxi/unit/add").authenticated();
                            http.requestMatchers("/api/v1/oxi/unit/update/{id}").authenticated();

                            // Ruta privada - Reseñas
                            http.requestMatchers("/api/v1/oxi/review/add").authenticated();
                            http.requestMatchers("/api/v1/oxi/review/update/{id}").authenticated();
                            http.requestMatchers("/api/v1/oxi/review/find").authenticated();
                            http.requestMatchers("/api/v1/oxi/review/all").authenticated();

                            // Rutas privadas - usuarios
                            http.requestMatchers("/api/v1/oxi/user/all").authenticated();
                            http.requestMatchers("/api/v1/oxi/user/find/{id}").authenticated();

                            http.requestMatchers("/api/v1/oxi/order/all/{state}").authenticated();
                            http.requestMatchers("/api/v1/oxi/order/user/{id}").authenticated();
                            http.requestMatchers("/api/v1/oxi/order/details/{id}").authenticated();
                            http.requestMatchers("/api/v1/oxi/order/find/{id}").authenticated();
                            http.requestMatchers("/api/v1/oxi/order/update/{id}").authenticated();
                            http.requestMatchers("/api/v1/oxi/order/add").authenticated();

                            http.anyRequest().authenticated();
                        })
                .addFilterBefore(new JwtValidator(jwtTokenProvider), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // Password Encoder: Hashear contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
