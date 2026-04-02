package org.insi.javaspring.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.insi.javaspring.model.Role;
import org.insi.javaspring.model.User;
import org.insi.javaspring.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(String firstName, String lastName, String email, String rawPassword, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("Email déjà utilisé");
        }
        User u = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email.toLowerCase().trim())
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(u);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return userRepository.findAll(pageable);
        }

        return userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword.trim(),
                        keyword.trim(),
                        keyword.trim(),
                        pageable
                );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public void updateRole(Long id, Role role) {
        User u = userRepository.findById(id).orElseThrow();
        u.setRole(role);
        userRepository.save(u);
    }

    public void setEnabled(Long id, boolean enabled) {
        User u = userRepository.findById(id).orElseThrow();
        u.setEnabled(enabled);
        userRepository.save(u);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}