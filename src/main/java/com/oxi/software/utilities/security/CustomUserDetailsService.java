package com.oxi.software.utilities.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lógica para cargar el usuario desde tu base de datos
        return org.springframework.security.core.userdetails.User.builder()
                .username("usuario")
                .password("contraseñaEncriptada")
                .roles("USER")
                .build();
    }
}