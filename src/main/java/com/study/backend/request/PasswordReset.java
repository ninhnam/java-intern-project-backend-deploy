package com.study.backend.request;


import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {
    private String old_password;
    private String new_password;
    private String confirm_password;

}
