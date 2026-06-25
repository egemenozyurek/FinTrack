package com.profileinsight.fintrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "e-mail cannot be blank")
    @Email(message = "Please type a valid e-mail address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
