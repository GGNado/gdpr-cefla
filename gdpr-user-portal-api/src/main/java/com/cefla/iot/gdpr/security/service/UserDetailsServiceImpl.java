package com.cefla.iot.gdpr.security.service;

import com.cefla.iot.gdpr.entity.auth.User;
import com.cefla.iot.gdpr.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService implementation that loads user details
 * from the database for Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username for authentication.
     * Supports both username and email as login identifiers.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user by username or email: {}", usernameOrEmail);

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    log.warn("User not found with username or email: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });

        log.debug("User found: {}, enabled: {}", user.getUsername(), user.getEnabled());

        if (!user.getEnabled()) {
            log.warn("User account is disabled: {}", user.getUsername());
            throw new UsernameNotFoundException("User account is disabled: " + user.getUsername());
        }

        return UserDetailsImpl.build(user);
    }

    /**
     * Load user by ID (useful for JWT token processing).
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UsernameNotFoundException("User not found with ID: " + id);
                });

        if (!user.getEnabled()) {
            log.warn("User account is disabled: {}", user.getUsername());
            throw new UsernameNotFoundException("User account is disabled: " + user.getUsername());
        }

        return UserDetailsImpl.build(user);
    }
}
