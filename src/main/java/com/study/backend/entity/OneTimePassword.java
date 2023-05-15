package com.study.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class OneTimePassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(name = "expiry_date_time", nullable = false)
    private LocalDateTime expiryDateTime;

    public OneTimePassword(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
