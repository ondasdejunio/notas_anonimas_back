package com.cursojava.curso.dtos;

import com.cursojava.curso.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BasicUserDataDTO {
    private String username;
    private Date dateBirth;
    private Gender gender;
}
