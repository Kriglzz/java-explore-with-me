package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @Size(min = 2, max = 250)
    @NotBlank
    private String name;

    @Size(min = 6, max = 254)
    @Email
    @NotBlank
    private String email;
}
