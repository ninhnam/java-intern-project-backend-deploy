package com.study.backend.service;

import com.study.backend.entity.OneTimePassword;
import com.study.backend.repository.OneTimePasswordRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OneTimePasswordService {

    private static final int OTP_EXPIRATION_MINUTES = 1;
    private static final int OTP_LENGTH = 6;

    private final OneTimePasswordRepository otpRepository;

    public OneTimePasswordService(OneTimePasswordRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public OneTimePassword generateOTP(String email) {
        // Generate a random OTP of length OTP_LENGTH
        String otp = RandomStringUtils.randomNumeric(OTP_LENGTH);

        // Calculate the expiry date/time of the OTP
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        // Save the OTP in the database
        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setEmail(email);
        oneTimePassword.setOtp(otp);
        oneTimePassword.setExpiryDateTime(expiryDateTime);
        return otpRepository.save(oneTimePassword);
    }

    public OneTimePassword setTimeAfterSendingEmail(OneTimePassword oneTimePassword) {
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
        oneTimePassword.setExpiryDateTime(expiryDateTime);
        return otpRepository.save(oneTimePassword);
    }

    public OneTimePassword setTimeAfterValidateOTP(String email, String otp) {
        Optional<OneTimePassword> otpOptional = otpRepository.findByEmailAndOtp(email, otp);
        if (otpOptional.isPresent()) {
            OneTimePassword oneTimePassword = otpOptional.get();
            LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(5);
            oneTimePassword.setExpiryDateTime(expiryDateTime);
            return otpRepository.save(oneTimePassword);
        }
        return null;
    }

    public String validateOTP(String email, String otp) {
        Optional<OneTimePassword> otpOptional = otpRepository.findByEmailAndOtp(email, otp);
        if (otpOptional.isPresent()) {
            OneTimePassword oneTimePassword = otpOptional.get();
            if (oneTimePassword.getExpiryDateTime().isBefore(LocalDateTime.now())) {
                // OTP has expired, delete it from the database and return false
                OneTimePassword oneTimePassword1 = otpRepository.findByEmail(email);
                return "OTP has expired";
            }
            // OTP is valid, delete it from the database and return true
            return "OTP is match";
        }
        return "OTP is not match";
    }

    public String validateOTPforResetPassword(String email, String otp) {
        Optional<OneTimePassword> otpOptional = otpRepository.findByEmailAndOtp(email, otp);
        if (otpOptional.isPresent()) {
            OneTimePassword oneTimePassword = otpOptional.get();
            if (oneTimePassword.getExpiryDateTime().isBefore(LocalDateTime.now())) {
                // OTP has expired, delete it from the database and return false
                OneTimePassword oneTimePassword1 = otpRepository.findByEmail(email);
                otpRepository.delete(oneTimePassword1);
                return "OTP has expired";
            }
            // OTP is valid, delete it from the database and return true
            otpRepository.delete(oneTimePassword);
            return "OTP is match";
        }
        return "OTP is not match";
    }

}