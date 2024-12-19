package com.youcode.ebanking.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            if (isTestProfile()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("Tentative de connexion avec un mot de passe incorrect pour : {}", username);
                throw new BadCredentialsException("Identifiants invalides");
            }

            if (!userDetails.isEnabled()) {
                log.warn("Tentative de connexion pour un compte désactivé : {}", username);
                throw new DisabledException("Compte utilisateur désactivé");
            }

            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        } catch (BadCredentialsException e) {
            log.error("Échec d'authentification pour l'utilisateur : {}", username);
            throw e;
        } catch (Exception e) {
            log.error("Erreur d'authentification pour l'utilisateur : {}", username, e);
            throw new AuthenticationServiceException("Erreur lors de l'authentification");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // Méthode pour vérifier si le profil 'test' est actif
    private boolean isTestProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }
}
