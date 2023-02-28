package com.cursojava.curso.dtos;

import com.cursojava.curso.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@Data
public class LoggedUserDataDTO {
    private String username;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateBirth;
    private Gender gender;

    public LoggedUserDataDTO(String username, String email, Date dateBirth, Gender gender) {
        this.username = username;
        this.email = email;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStr = formatter.format(dateBirth);
        System.out.println(""+ dateBirth.toString());
        this.dateBirth = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.gender = gender;
    }
}
