package com.spboot.auth.security.config;

import com.spboot.auth.config.AppConstants;
import com.spboot.auth.entity.Role;
import com.spboot.auth.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRole("ROLE_" + AppConstants.ADMIN_ROLE);
        createRole("ROLE_" + AppConstants.USER_ROLE);
        createRole("ROLE_" + AppConstants.GUEST_ROLE);
    }

    private void createRole(String roleName) {
        roleRepository.findByName(roleName)
                .ifPresentOrElse(
                        role -> System.out.println(roleName + " already exists"),
                        () -> {
                            Role newRole = new Role();
                            newRole.setName(roleName);
                            roleRepository.save(newRole);
                            System.out.println(roleName + " created");
                        }
                );
    }
}
