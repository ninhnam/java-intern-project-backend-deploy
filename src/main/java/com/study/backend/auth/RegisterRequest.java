package com.study.backend.auth;

import com.study.backend.user.value.Account;
import com.study.backend.user.value.Gender;
import com.study.backend.user.value.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String email;
  private String password;
  private Gender gender;
  private Role role;
  private Account account;
}
