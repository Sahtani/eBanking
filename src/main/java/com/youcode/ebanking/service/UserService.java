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

    public UserResponseDTO registerNewUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsEbUserByUsername(registrationDTO.username())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + registrationDTO.username());
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


    public UserResponseDTO changeUserRole(String username, String newRoleName) {
        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + newRoleName));

        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.setRole(newRole);

        EbUser updatedUser = userRepository.save(user);

        return userMapper.userToUserResponseDTO(updatedUser);
    }


    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        if (passwordChangeDTO.currentPassword() == null || passwordChangeDTO.newPassword() == null) {
            throw new IllegalArgumentException("Passwords cannot be null");
        }

        if(passwordChangeDTO.currentPassword().equals(passwordChangeDTO.newPassword())) {
            throw new BadCredentialsException("Le nouveau mot de passe ne peut pas être identique à l'ancien mot de passe.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userAuth = authentication.getName();

        EbUser user = userRepository.findByUsername(userAuth)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(passwordChangeDTO.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDTO.newPassword()));
        userRepository.save(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponseDTO).collect(Collectors.toList());
    }

    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::userToUserResponseDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void deleteUser(String username) {
        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        userRepository.delete(user);
    }
}