package com.becajava.userservice.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @NotBlank(message = "O nome do usuário é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ter um formato válido")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
