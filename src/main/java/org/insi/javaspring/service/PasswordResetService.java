package org.insi.javaspring.service;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.auth.PasswordResetOtp;
import org.insi.javaspring.repository.PasswordResetOtpRepository;
import org.insi.javaspring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Transactional
    public void sendOtp(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        otpRepository.deleteByEmail(email);

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        PasswordResetOtp entity = new PasswordResetOtp();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));

        otpRepository.save(entity);

        mailService.send(
                email,
                "Code OTP de réinitialisation",
                "Votre code OTP est : " + otp + "\nIl expire dans 10 minutes."
        );
    }

    public boolean verifyOtp(String email, String otp) {
        var otpOpt = otpRepository.findByEmail(email);
        if (otpOpt.isEmpty()) return false;

        PasswordResetOtp entity = otpOpt.get();

        if (Instant.now().isAfter(entity.getExpiresAt())) {
            return false;
        }

        return entity.getOtp().equals(otp);
    }

    public void resetPasswordByEmail(String email, String newPassword) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.deleteByEmail(email);
    }
}