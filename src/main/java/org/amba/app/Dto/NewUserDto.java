package org.amba.app.Dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


@Data
@AllArgsConstructor
public class NewUserDto {
    private UUID userId;


    @Email(message = "Please provide a valid email address")
    @Column(unique = true, nullable = false)
    private String email;


    // Hashed password is stored
    private String password;
}
