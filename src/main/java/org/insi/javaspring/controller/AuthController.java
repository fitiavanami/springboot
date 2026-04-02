package org.insi.javaspring.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.insi.javaspring.dto.RegisterForm;
import org.insi.javaspring.model.Role;
import org.insi.javaspring.repository.UserRepository;
import org.insi.javaspring.service.JwtService;
import org.insi.javaspring.service.PasswordResetService;
import org.insi.javaspring.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult br) {

        if (br.hasErrors()) {
            return "auth/register";
        }

        userService.register(
                form.getFirstName(),
                form.getLastName(),
                form.getEmail(),
                form.getPassword(),
                Role.AGENT_ENREGISTREMENT
        );

        return "redirect:/auth/login?registered";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam String email) {
        passwordResetService.sendOtp(email);
        return "redirect:/auth/verify-otp?email=" + email;
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtpPost(@RequestParam String email,
                                @RequestParam String otp,
                                RedirectAttributes redirectAttributes) {
        boolean valid = passwordResetService.verifyOtp(email, otp);

        if (!valid) {
            redirectAttributes.addFlashAttribute("error", "Code OTP invalide ou expiré");
            return "redirect:/auth/verify-otp?email=" + email;
        }

        return "redirect:/auth/reset-password?email=" + email;
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordPost(@RequestParam String email,
                                    @RequestParam String newPassword) {
        passwordResetService.resetPasswordByEmail(email, newPassword);
        return "redirect:/auth/login?reset";
    }

    @PostMapping("/token")
    @ResponseBody
    public ResponseEntity<?> generateToken(@Valid @RequestBody LoginRequest request) {

        var userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Utilisateur introuvable"));
        }

        var user = userOpt.get();

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of("error", "Compte désactivé"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }
}