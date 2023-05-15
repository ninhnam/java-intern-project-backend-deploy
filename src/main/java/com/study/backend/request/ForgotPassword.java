package com.study.backend.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPassword {
    private String email;
    private String otp;
    private String new_password;
    private String new_password_confirm;
}
