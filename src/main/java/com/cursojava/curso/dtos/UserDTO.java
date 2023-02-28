package com.cursojava.curso.dtos;

import com.cursojava.curso.enums.Gender;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class UserDTO {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Email no válido")
    private String email;
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
    @NotBlank(message = "La contraseña nueva no puede estar vacía")
    private String newPassword;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateBirth;
    @NotBlank(message = "El género no puede estar vacío")
    private Gender gender;
}
