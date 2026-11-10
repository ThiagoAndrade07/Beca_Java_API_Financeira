package com.becajava.userservice.adapter.in.web.controller;

import com.becajava.userservice.adapter.in.csv.UserCsvParser;
import com.becajava.userservice.adapter.in.web.dto.ImportUsersResponse;
import com.becajava.userservice.adapter.in.web.mapper.ImportUsersWebMapper;
import com.becajava.userservice.application.exception.InvalidCsvFileException;
import com.becajava.userservice.application.model.ImportUserCommand;
import com.becajava.userservice.application.model.ImportUsersResult;
import com.becajava.userservice.port.in.ImportUsersUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.becajava.userservice.adapter.in.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;

@Tag(name = "User Import", description = "Importação de usuários por arquivo CSV")
@RestController
@RequestMapping("/users")
public class UserImportController {

    private final UserCsvParser userCsvParser;
    private final ImportUsersUseCase importUsersUseCase;

    public UserImportController(UserCsvParser userCsvParser, ImportUsersUseCase importUsersUseCase) {
        this.userCsvParser = userCsvParser;
        this.importUsersUseCase = importUsersUseCase;
    }

    @Operation(
            summary = "Importar usuários por CSV",
            description = "Importa as linhas válidas e informa as linhas rejeitadas e seus motivos.",
            security = @SecurityRequirement(
                    name = OpenApiConfig.BASIC_AUTH
            )
    )
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportUsersResponse> importUsers(
            @Parameter(description = "Arquivo CSV com o cabeçalho name,email,password", required = true)
            @RequestParam("file") MultipartFile file
    ) {

        if (file.isEmpty()) {
            throw new InvalidCsvFileException("O arquivo CSV está vazio");
        }

        List<ImportUserCommand> commands;

        try {
            commands = userCsvParser.parse(file.getInputStream());
        } catch (IOException exception) {
            throw new InvalidCsvFileException("O arquivo CSV não pôde ser processado", exception);
        }

        ImportUsersResult result = importUsersUseCase.importUsers(commands);

        ImportUsersResponse response = ImportUsersWebMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }


}
