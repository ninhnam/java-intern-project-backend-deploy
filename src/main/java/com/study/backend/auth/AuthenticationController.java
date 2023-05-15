package com.study.backend.auth;

import com.study.backend.exception.BadRequestException;
import com.study.backend.exception.ForbiddenException;
import com.study.backend.exception.NotFoundException;
import com.study.backend.request.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ResponseData<String>> handleBadRequestException(BadRequestException ex) {
    ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ResponseData<String>> handleNotFoundException(NotFoundException ex) {
    ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ResponseData<String>> handleForbiddenException(NotFoundException ex) {
    ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  @PostMapping("/register")
  public ResponseEntity<ResponseData<AuthenticationResponse>> register(
      @RequestBody RegisterRequest request
  ) throws Exception {
    ResponseData<AuthenticationResponse> response = new ResponseData<>("Success", service.register(request));
    return ResponseEntity.ok(response);
  }

//  @GetMapping("/user")
//  public String user(OAuth2AuthenticationToken token, Model model) {
//    String name = token.getPrincipal().getAttribute("name");
//    String email = token.getPrincipal().getAttribute("email");
//    String picture = token.getPrincipal().getAttribute("picture");
//    String id = token.getPrincipal().getAttribute("sub");
//    model.addAttribute("name", name);
//    model.addAttribute("email", email);
//    model.addAttribute("picture", picture);
//    model.addAttribute("id", id);
//    return "user";
//  }

  @PostMapping("/login")
  public ResponseEntity<ResponseData<AuthenticationResponse>> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    ResponseData<AuthenticationResponse> response = new ResponseData<>("Success", service.authenticate(request));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<ResponseData<AuthenticationResponse>> refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    ResponseData<AuthenticationResponse> data = new ResponseData<>("Success", service.refreshToken(request, response));
    return ResponseEntity.ok(data);
  }


}
