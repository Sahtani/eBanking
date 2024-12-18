package com.youcode.ebanking.service;

import com.youcode.ebanking.dto.*;
import com.youcode.ebanking.exception.UsernameAlreadyExistsException;
import com.youcode.ebanking.mapper.UserMapper;
import com.youcode.ebanking.model.EbUser;
import com.youcode.ebanking.model.Role;
import com.youcode.ebanking.repository.RoleRepository;
import com.youcode.ebanking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;


    /**
     * Inscription d'un nouvel utilisateur
     *
     * @param registrationDTO Données d'inscription
     * @return Utilisateur enregistré
     * @throws UsernameAlreadyExistsException si name d'utilisateur existe déjà
     */

    public UserResponseDTO registerNewUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsEbUserByUsername(registrationDTO.email())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + registrationDTO.email());
        }

        Role userRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        EbUser newUser = new EbUser();
        newUser.setUsername(registrationDTO.username()).
                setEmail(registrationDTO.email()).setPassword(passwordEncoder.encode(registrationDTO.password()))
                .setRole(userRole).setEnabled(true);
        EbUser savedUser = userRepository.save(newUser);


        return userMapper.userToUserResponseDTO(savedUser);
    }

    /**
     * Méthode de login sans (Authentification de base)
     * @param loginRequest
     * @return Un message indiquant le succès de la connexion
     */
    public String login(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        EbUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        RoleEmbeddableDto roleDto = toRoleEmbeddableDto(user.getRole());
        return "Connexion réussie pour l'utilisateur : " + loginRequest.username();
    }

    public RoleEmbeddableDto toRoleEmbeddableDto(Role role) {
        return new RoleEmbeddableDto(role.getName());
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("Utilisateur déconnecté");
    }

    /**
     * Changer le rôle d'un utilisateur
     *
     * @param username    Nom d'utilisateur
     * @param newRoleName Nouveau nom de rôle
     * @return Utilisateur mis à jour
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     * @throws RuntimeException          si le rôle n'existe pas
     */
    public UserResponseDTO changeUserRole(String username, String newRoleName) {
        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + newRoleName));

        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.setRole(newRole);

        EbUser updatedUser = userRepository.save(user);

        return userMapper.userToUserResponseDTO(updatedUser);
    }

    /**
     * Changer le mot de passe d'un utilisateur
     *
     * @param username          Nom d'utilisateur
     * @param passwordChangeDTO Informations pour le changement de mot de passe
     * @return Utilisateur mis à jour
     * @throws BadCredentialsException si le mot de passe actuel est incorrect
     */
    public UserResponseDTO changePassword(String username, PasswordChangeDTO passwordChangeDTO) {
        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(passwordChangeDTO.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDTO.newPassword()));
        EbUser updatedUser = userRepository.save(user);

        return userMapper.userToUserResponseDTO(updatedUser);
    }

    /**
     * Récupérer tous les utilisateurs (pour l'admin)
     *
     * @return Liste de tous les utilisateurs
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponseDTO).collect(Collectors.toList());
    }

    /**
     * Récupérer un utilisateur par son nom d'utilisateur
     *
     * @param username Nom d'utilisateur
     * @return Utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::userToUserResponseDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Supprimer un utilisateur
     *
     * @param username Nom d'utilisateur à supprimer
     */
    public void deleteUser(String username) {
        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        userRepository.delete(user);
    }
}