package com.study.backend.repository;

import com.study.backend.entity.OneTimePassword;
import com.study.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {

    Optional<OneTimePassword> findByEmailAndOtp(String email, String otp);

    OneTimePassword findByEmail(String email);

}