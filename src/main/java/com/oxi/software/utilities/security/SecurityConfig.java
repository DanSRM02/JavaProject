//package com.oxi.software.utilities.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.util.List;
//
//@Configuration
//public class SecurityConfig {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailsService userDetailsService;
//
//    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/auth/**").permitAll() // Permitir acceso sin autenticación a rutas públicas
//                        .anyRequest().authenticated()) // Rutas protegidas requieren autenticación
//                .addFilterBefore(new JwtValidator(jwtTokenProvider, userDetailsService),
//                        UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//}
