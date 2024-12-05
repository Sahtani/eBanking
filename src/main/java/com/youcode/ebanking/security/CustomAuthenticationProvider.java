package com.youcode.ebanking.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("Tentative de connexion avec un mot de passe incorrect pour : {}", username);
                throw new BadCredentialsException("Identifiants invalides");
            }

            if (!userDetails.isEnabled()) {
                log.warn("Tentative de connexion pour un compte désactivé : {}", username);
                throw new DisabledException("Compte utilisateur désactivé");
            }

            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    password,
                    userDetails.getAuthorities()
            );

        } catch (BadCredentialsException e) {
            log.error("Échec d'authentification pour l'utilisateur : {}", username);
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}