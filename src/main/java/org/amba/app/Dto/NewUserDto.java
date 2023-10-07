package org.amba.app.Dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String password;

}
