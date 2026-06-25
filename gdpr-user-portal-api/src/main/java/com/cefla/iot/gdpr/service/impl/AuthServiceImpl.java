package com.cefla.iot.gdpr.service.impl;

import com.cefla.iot.gdpr.dto.request.auth.LoginRequest;
import com.cefla.iot.gdpr.dto.request.auth.RegisterRequest;
import com.cefla.iot.gdpr.dto.response.MessageResponse;
import com.cefla.iot.gdpr.dto.response.jwt.JwtResponse;
import com.cefla.iot.gdpr.entity.auth.Role;
import com.cefla.iot.gdpr.entity.auth.RoleName;
import com.cefla.iot.gdpr.entity.auth.User;
import com.cefla.iot.gdpr.mapper.UtenteMapper;
import com.cefla.iot.gdpr.repository.auth.RoleRepository;
import com.cefla.iot.gdpr.repository.auth.UserRepository;
import com.cefla.iot.gdpr.security.jwt.JwtUtils;
import com.cefla.iot.gdpr.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for handling authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UtenteMapper userMapper;

    /**
     * Authenticate user and generate JWT token.
     */
    @Transactional(readOnly = true)
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("User authenticated successfully: {}", userDetails.getUsername());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                roles,
                jwtUtils.getJwtExpirationMs());
    }

    /**
     * Register a new user.
     */
    @Transactional
    public MessageResponse registerUser(RegisterRequest signUpRequest) {
        log.info("Registering new user: {}", signUpRequest.getUsername());

        // Check if username exists
        if (userRepository.existsByUsername((signUpRequest.getUsername()))) {
            log.warn("Username already exists: {}", signUpRequest.getUsername());
            return MessageResponse.error("Error: Username is already taken!");
        }

        // Check if email exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            log.warn("Email already exists: {}", signUpRequest.getEmail());
            return MessageResponse.error("Error: Email is already in use!");
        }

        // Create new user account
        User user = userMapper.convert(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));


        // Set user roles
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role
            Role userRole = roleRepository.findByName((RoleName.ROLE_USER.name()))
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                System.out.println(role);
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        userRepository.save(user);

        log.info("User registered successfully: {}", user.getUsername());
        return MessageResponse.success("User registered successfully!");
    }

    /**
     * Check if username is available.
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Check if email is available.
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Refresh JWT token.
     */
    @Transactional(readOnly = true)
    public JwtResponse refreshToken(String username) {
        log.info("Refreshing token for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwt = jwtUtils.generateTokenFromUser(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                roles,
                jwtUtils.getJwtExpirationMs());
    }


    public boolean validateToken(String token) {
        return jwtUtils.validateJwtToken(token);
    }
}
