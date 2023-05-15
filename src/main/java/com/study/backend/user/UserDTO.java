package com.study.backend.user;

import com.study.backend.user.value.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private Gender gender;
    private Role role;
    private Account account;
}
