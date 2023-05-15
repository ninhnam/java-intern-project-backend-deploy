package com.study.backend.controller;

import com.study.backend.entity.OneTimePassword;
import com.study.backend.exception.ForbiddenException;
import com.study.backend.request.ForgotPassword;
import com.study.backend.request.ValidateOTP;
import com.study.backend.service.EmailService;
import com.study.backend.service.OneTimePasswordService;
import com.study.backend.exception.BadRequestException;
import com.study.backend.exception.NotFoundException;
import com.study.backend.request.PasswordReset;
import com.study.backend.request.ResponseData;
import com.study.backend.service.UserService;
import com.study.backend.user.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OneTimePasswordService oneTimePasswordService;
    @Autowired
    private EmailService emailService;


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

    @GetMapping
    public ResponseEntity<ResponseData<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        ResponseData<List<UserDTO>> response = new ResponseData<>("Success", users);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseData<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO newUser = userService.createUser(userDTO);
        ResponseData<UserDTO> response = new ResponseData<>("Create success", newUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/resetpassword/{id}")
    public ResponseEntity<ResponseData<UserDTO>> changePassword(@Valid @RequestBody PasswordReset passwordReset, @PathVariable Long id) {
        UserDTO userDTO = userService.updatePassword(id, passwordReset);
        ResponseData<UserDTO> response = new ResponseData<>("Success", userDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserDTOById(id);
        ResponseData<UserDTO> response = new ResponseData<>("Success", userDTO);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseData<UserDTO>> getUserByEmail(@PathVariable String email) {
        UserDTO userDTO = userService.getUserDTOByEmail(email);
        ResponseData<UserDTO> response = new ResponseData<>("Success", userDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<UserDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        userService.updateUser(userDTO, id);
        ResponseData<UserDTO> response = new ResponseData<>("Edit success", userDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        ResponseData<String> response = new ResponseData<>("Delete success", "Delete user with id = " + id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/generate/{email}")
    public ResponseEntity<ResponseData<String>> forgotPassword(@PathVariable String email) {
        UserDTO userDTO = userService.getUserDTOByEmail(email);
        if(userDTO == null) {
            return ResponseEntity.ok(new ResponseData<>("Failed", "System not have account with this email!"));
        }
        OneTimePassword oneTimePassword = oneTimePasswordService.generateOTP(email);
        try {
            emailService.sendEmail(email, "INTERN JAVA PROJECT", "OTP to reset password is: " + oneTimePassword.getOtp());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        OneTimePassword newOneTimePassword = oneTimePasswordService.setTimeAfterSendingEmail(oneTimePassword);
        ResponseData<String> response = new ResponseData<>("Success", "new OTP has sended in your email");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/forgot-password/validate")
    public ResponseEntity<ResponseData<String>> validateotp(@RequestBody ValidateOTP validateOTP) {
        String otpStatus = oneTimePasswordService.validateOTP(validateOTP.getEmail(), validateOTP.getOtp());
        if(otpStatus.equals("OTP is match")) {
            OneTimePassword newOneTimePassword = oneTimePasswordService.setTimeAfterValidateOTP(validateOTP.getEmail(), validateOTP.getOtp());
        } else if (otpStatus.equals("OTP is not match")) {
            return ResponseEntity.ok(new ResponseData<>("Failed", "OTP is not match"));
        } else if (otpStatus.equals("OTP has expired")) {
            return ResponseEntity.ok(new ResponseData<>("Failed", "OTP has expired"));
        }
        ResponseData<String> response = new ResponseData<>("Success", "OTP is match");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/forgot-password/reset")
    public ResponseEntity<ResponseData<String>> changeForgotPassword(@RequestBody ForgotPassword forgotPassword) {
        String otpStatus = oneTimePasswordService.validateOTPforResetPassword(forgotPassword.getEmail(), forgotPassword.getOtp());
        if(otpStatus.equals("OTP is match")) {
            userService.updateForgotPasswordUser(forgotPassword.getEmail(), forgotPassword.getNew_password(), forgotPassword.getNew_password_confirm());
        } else if (otpStatus.equals("OTP is not match")) {
            return ResponseEntity.ok(new ResponseData<>("Failed", "OTP is not match"));
        } else if (otpStatus.equals("OTP has expired")) {
            return ResponseEntity.ok(new ResponseData<>("Failed", "OTP has expired"));
        }
        ResponseData<String> response = new ResponseData<>("Success", "Update password success, new password: " + forgotPassword.getNew_password());
        return ResponseEntity.ok(response);
    }
}