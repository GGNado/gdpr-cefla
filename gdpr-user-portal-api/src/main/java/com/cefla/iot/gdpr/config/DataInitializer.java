package com.cefla.iot.gdpr.config;

import com.cefla.iot.gdpr.entity.auth.Role;
import com.cefla.iot.gdpr.entity.auth.RoleName;
import com.cefla.iot.gdpr.entity.auth.User;
import com.cefla.iot.gdpr.repository.auth.RoleRepository;
import com.cefla.iot.gdpr.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional("authTransactionManager")
    public void run(String... args) {
        log.info("Starting DataInitializer for Auth Database...");

        if (roleRepository.count() == 0) {
            Role roleUser = new Role();
            roleUser.setName(RoleName.ROLE_USER.name());
            roleUser.setDescription(RoleName.ROLE_USER.getDescription());

            Role roleAdmin = new Role();
            roleAdmin.setName(RoleName.ROLE_ADMIN.name());
            roleAdmin.setDescription(RoleName.ROLE_ADMIN.getDescription());

            log.info("Creating default roles...");
            roleRepository.save(roleUser);
            roleRepository.save(roleAdmin);
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN.name())
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));

        if (!userRepository.existsByUsername("Guido.vigliotti@cefla.it")) {
            log.info("Creating default admin user: Guido.vigliotti@cefla.it");

            User guido = User.builder()
                    .username("Guido.vigliotti@cefla.it")
                    .email("Guido.vigliotti@cefla.it")
                    .firstName("Guido")
                    .lastName("Vigliotti")
                    .password(passwordEncoder.encode("tZWsKGVEdrm4vswA"))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();

            guido.addRole(adminRole);
            userRepository.save(guido);
        }

        if (!userRepository.existsByUsername("luigimassa")) {
            log.info("Creating default admin user: luigimassa");

            User luigi = User.builder()
                    .username("luigimassa")
                    .email("luigi.massa@cefla.it")
                    .firstName("Luigi")
                    .lastName("Massa")
                    .password(passwordEncoder.encode("yz4nylpQOkdvK3n2"))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();

            luigi.addRole(adminRole);
            userRepository.save(luigi);
        }

        log.info("DataInitializer completed.");
    }
}