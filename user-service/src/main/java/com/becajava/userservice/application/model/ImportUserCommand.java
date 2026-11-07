package com.becajava.userservice.application.model;

import com.becajava.userservice.application.validation.Utf8ByteSize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ImportUserCommand {

    private final int lineNumber;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private final String name;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ter um formato válido")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres")
    private final String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres")
    @Utf8ByteSize(max = 72, message = "A senha deve ter no máximo 72 bytes em UTF-8")
    private final String password;

    public ImportUserCommand(int lineNumber, String name, String email, String password) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
