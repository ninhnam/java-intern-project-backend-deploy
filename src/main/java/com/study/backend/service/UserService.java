package com.study.backend.service;

import com.study.backend.user.User;
import com.study.backend.exception.BadRequestException;
import com.study.backend.exception.NotFoundException;
import com.study.backend.repository.UserRepository;
import com.study.backend.request.PasswordReset;
import com.study.backend.user.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getGender(), user.getRole(), user.getAccount()))
                .collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("Email " + userDTO.getEmail() + " taken");
        }
        if (userDTO.getPassword().length() < 8 && userDTO.getPassword().length() > 20) {
            throw new BadRequestException("Password invalid!!!");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setGender(userDTO.getGender());
        user.setRole(userDTO.getRole());
        user.setAccount(userDTO.getAccount());

        userRepository.save(user);
        return new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getGender(), user.getRole(), user.getAccount());
    }

    public void updateUser(@Valid UserDTO userDTO, Long id) {
        User user = getUserById(id);
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setGender(userDTO.getGender());
        user.setRole(userDTO.getRole());
        user.setAccount(userDTO.getAccount());
        userRepository.save(user);
    }

    public void updateForgotPasswordUser(String email, String new_password, String new_password_confirm) {
        if(new_password.length() < 8 && new_password.length() > 20) {
            throw new BadRequestException("New password invalid!");
        }
        if(!new_password.equals(new_password_confirm)) {
            throw new BadRequestException("New password not match with confirm of it!");
        }
        User user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(new_password));
        userRepository.save(user);
    }

    public UserDTO updatePassword(Long id, @Valid PasswordReset passwordReset) {
        User user = getUserById(id);
        if (!passwordEncoder.matches(passwordReset.getOld_password(), user.getPassword())) {
            throw new BadRequestException("Old password is not match");
        }
        if (!passwordReset.getNew_password().equals(passwordReset.getConfirm_password())) {
            throw new BadRequestException("New Password and Password Confirm is not match");
        }
        passwordReset.setNew_password(passwordEncoder.encode(passwordReset.getNew_password()));
        passwordReset.setConfirm_password(passwordEncoder.encode(passwordReset.getConfirm_password()));
        user.setPassword(passwordReset.getNew_password());
        userRepository.save(user);

        return new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getGender(), user.getRole(), user.getAccount());
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("User with id: " + id + " not existing");
        }
        return user;
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("User with id: " + id + " not existing");
        }
        return new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getGender(), user.getRole(), user.getAccount());
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User with email: " + email + " not existing");
        }
        return user;
    }

    public UserDTO getUserDTOByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User with email: " + email + " not existing");
        }
        return new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getGender(), user.getRole(), user.getAccount());
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("User with id: " + id + " not existing");
        }
        userRepository.deleteById(id);
    }
}