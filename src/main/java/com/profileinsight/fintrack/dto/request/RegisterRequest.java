package com.profileinsight.fintrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "E-mail cannot be blank")
    @Email(message = "Please type a valid e-mail address")
    private String email;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, message = "Password must be 8 characters at least")
    private String password;
}
