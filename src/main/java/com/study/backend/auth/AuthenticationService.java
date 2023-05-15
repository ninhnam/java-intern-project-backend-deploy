package com.study.backend.auth;

import com.study.backend.config.JwtService;
import com.study.backend.exception.ForbiddenException;
import com.study.backend.repository.UserRepository;
import com.study.backend.token.Token;
import com.study.backend.token.TokenRepository;
import com.study.backend.token.TokenType;
import com.study.backend.user.CustomUserDetails;
import com.study.backend.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) throws Exception {
    var userPresent = repository.findByEmail(request.getEmail());
    if(userPresent != null) {
      throw new Exception("this email was registered");
    }
    var user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .gender(request.getGender())
        .role(request.getRole())
        .account(request.getAccount())
        .build();
    System.out.println("User: " + user);
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
    System.out.println("Token " + jwtToken);
    var refreshToken = jwtService.generateRefreshToken(new CustomUserDetails(user));
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

//login
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail());
    var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
    var refreshToken = jwtService.generateRefreshToken(new CustomUserDetails(user));
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      throw new ForbiddenException("Not have auth token or auth token not start with Bearer!");
    }
    refreshToken = authHeader.substring(7);
    System.out.println("refreshToken " +  refreshToken);
    userEmail = jwtService.extractUsername(refreshToken);
    System.out.println("userEmail " + userEmail);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail);
      if (jwtService.isTokenValid(refreshToken, new CustomUserDetails(user))) {
        var accessToken = jwtService.generateToken(new CustomUserDetails(user));
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
      }
    }
    return null;
  }
}
