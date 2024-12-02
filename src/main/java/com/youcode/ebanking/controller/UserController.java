package com.youcode.ebanking.controller;

import com.youcode.ebanking.dto.UserDTO;
import com.youcode.ebanking.dto.UserRegistrationDTO;
import com.youcode.ebanking.dto.UserUpdateRoleDTO;
import com.youcode.ebanking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO user = userService.registerNewUser(registrationDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{username}/updateRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(@Valid @RequestBody UserUpdateRoleDTO updateRoleDTO) {
        UserDTO user = userService.changeUserRole(updateRoleDTO.username(), updateRoleDTO.roleName());
        return ResponseEntity.ok(user);
    }
}