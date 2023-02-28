package com.cursojava.curso.controllers;
import com.cursojava.curso.dtos.LoggedUserDataDTO;
import com.cursojava.curso.dtos.UserDTO;
import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.UserRepository;
import com.cursojava.curso.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserRepository userRepository;

        @PostMapping("/register")
        public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
            try {
                User existingUser = userRepository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail());
                if(existingUser != null){
                    return new ResponseEntity<>("Ya existe una cuenta con ese nombre de usuario o email", HttpStatus.BAD_REQUEST);
                }
                User user = new User();
                user.setUsername(userDTO.getUsername());
                user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
                user.setEmail(userDTO.getEmail());
                user.setDateBirth(userDTO.getDateBirth());
                user.setGender(userDTO.getGender());
                userRepository.save(user);
                return new ResponseEntity<>("Usuario registrado exitosamente", HttpStatus.CREATED);
            } catch (Exception e){
                return new ResponseEntity<>("Error al registrar el usuario: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@RequestBody UserDTO userDTO) {
        try {
            User userLogged = AuthenticationUtils.getUserLogged();
            if(!userDTO.getEmail().isEmpty() && !Objects.equals(userLogged.getEmail(), userDTO.getEmail())){
                userLogged.setEmail(userDTO.getEmail());
            }
            if(!userDTO.getPassword().isEmpty()){
                if (!new BCryptPasswordEncoder().matches(userDTO.getPassword(), userLogged.getPassword())){
                    return new ResponseEntity<>("La contraseña actual no coincide", HttpStatus.BAD_REQUEST);
                }
                if(new BCryptPasswordEncoder().matches(userDTO.getNewPassword(), userLogged.getPassword())){
                    return new ResponseEntity<>("La nueva contraseña debe ser diferente a la actual", HttpStatus.BAD_REQUEST);
                }
                userLogged.setPassword(new BCryptPasswordEncoder().encode(userDTO.getNewPassword()));
            }
            if(userDTO.getDateBirth() != null && !Objects.equals(userLogged.getDateBirth(), userDTO.getDateBirth())){
                userLogged.setDateBirth(userDTO.getDateBirth());
            }
            if(userDTO.getGender() != null && !Objects.equals(userLogged.getGender(), userDTO.getGender())){
                userLogged.setGender(userDTO.getGender());
            }
            userRepository.save(userLogged);
            LoggedUserDataDTO user = new LoggedUserDataDTO(userLogged.getUsername(), userLogged.getEmail(), userLogged.getDateBirth(), userLogged.getGender());
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            System.out.println("" + e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
