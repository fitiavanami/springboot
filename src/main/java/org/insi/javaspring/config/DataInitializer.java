package org.insi.javaspring.config;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.model.Role;
import org.insi.javaspring.model.User;
import org.insi.javaspring.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 🔥 Si aucun ADMIN n'existe
        if (!userRepository.existsByRole(Role.ADMIN)) {

            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@cin.local")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);

            System.out.println("======================================");
            System.out.println("ADMIN PAR DEFAUT CREE");
            System.out.println("Email: admin@cin.local");
            System.out.println("Password: admin123");
            System.out.println("======================================");
        }
    }
}