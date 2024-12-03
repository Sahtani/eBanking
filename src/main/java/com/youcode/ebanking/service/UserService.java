package com.youcode.ebanking.service;

import com.youcode.ebanking.dto.PasswordChangeDTO;
import com.youcode.ebanking.dto.UserDTO;
import com.youcode.ebanking.dto.UserRegistrationDTO;
import com.youcode.ebanking.exception.UserAlreadyExistsByEmailException;
import com.youcode.ebanking.mapper.UserMapper;
import com.youcode.ebanking.model.EbUser;
import com.youcode.ebanking.model.Role;
import com.youcode.ebanking.repository.RoleRepository;
import com.youcode.ebanking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.logging.Logger;
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;


    /**
     * Inscription d'un nouvel utilisateur
     *
     * @param registrationDTO Données d'inscription
     * @return Utilisateur enregistré
     * @throws UserAlreadyExistsByEmailException si email d'utilisateur existe déjà
     */

    public UserDTO registerNewUser(UserRegistrationDTO registrationDTO) {

        if (userRepository.existsEbUserByEmail(registrationDTO.email())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + registrationDTO.email());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        EbUser newUser = new EbUser();
        newUser.setUsername(registrationDTO.username()).
                setEmail(registrationDTO.email()).setPassword(passwordEncoder.encode(registrationDTO.password()))
                .setRole(userRole).setEnabled(true);
        EbUser savedUser = userRepository.save(newUser);


        return userMapper.userToUserDTO(savedUser);
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
    @Transactional
    public UserDTO changeUserRole(String username, String newRoleName) {
        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + newRoleName));

        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.setRole(newRole);

        EbUser updatedUser = userRepository.save(user);

        return userMapper.userToUserDTO(updatedUser);
    }

    /**
     * Changer le mot de passe d'un utilisateur
     *
     * @param username          Nom d'utilisateur
     * @param passwordChangeDTO Informations pour le changement de mot de passe
     * @return Utilisateur mis à jour
     * @throws BadCredentialsException si le mot de passe actuel est incorrect
     */
    @Transactional
    public UserDTO changePassword(String username, PasswordChangeDTO passwordChangeDTO) {
        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(passwordChangeDTO.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDTO.newPassword()));
        EbUser updatedUser = userRepository.save(user);

        return userMapper.userToUserDTO(updatedUser);
    }

    /**
     * Récupérer tous les utilisateurs (pour l'admin)
     *
     * @return Liste de tous les utilisateurs
     */
    public List<EbUser> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Récupérer un utilisateur par son nom d'utilisateur
     *
     * @param username Nom d'utilisateur
     * @return Utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    public EbUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Supprimer un utilisateur
     *
     * @param username Nom d'utilisateur à supprimer
     */
    @Transactional
    public void deleteUser(String username) {
        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        userRepository.delete(user);
    }
}