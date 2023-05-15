package com.study.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors()
            .and()
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers("/api/v1/auth/**")
          .permitAll()
        .requestMatchers("/api/v1/users/forgot-password/**").permitAll()
            //user controller
        .requestMatchers(HttpMethod.GET,"/api/v1/users").hasAnyAuthority("ROLE_ADMIN")
        .requestMatchers(HttpMethod.POST,"/api/v1/users").permitAll()
        .requestMatchers(HttpMethod.PUT,"/api/v1/users/resetpassword/**").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/v1/users/**").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/v1/users/email/**").permitAll()
        .requestMatchers(HttpMethod.PUT,"/api/v1/users/**").permitAll()
        .requestMatchers(HttpMethod.DELETE,"/api/v1/users/**").hasAnyAuthority("ROLE_ADMIN")
            //product controller
        .requestMatchers(HttpMethod.POST,"/api/v1/products/**").hasAnyAuthority("ROLE_SALESMAN")
        .requestMatchers(HttpMethod.GET,"/api/v1/products/user/**").hasAnyAuthority("ROLE_SALESMAN")
        .requestMatchers(HttpMethod.PUT,"/api/v1/products/**").hasAnyAuthority("ROLE_SALESMAN")
        .requestMatchers(HttpMethod.DELETE,"/api/v1/products/**").hasAnyAuthority("ROLE_SALESMAN")
        .requestMatchers(HttpMethod.GET, "/api/v1/products").hasAnyAuthority("ROLE_SALESMAN", "ROLE_USER")
            //sort controller
        .requestMatchers("/api/v1/sorts/**").hasAnyAuthority("ROLE_SALESMAN", "ROLE_USER")
        .anyRequest()
          .authenticated()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout()
        .logoutUrl("/api/v1/auth/logout")
        .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
    ;

    return http.build();
  }

}
