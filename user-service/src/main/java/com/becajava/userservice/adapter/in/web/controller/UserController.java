package com.becajava.userservice.adapter.in.web.controller;

import com.becajava.userservice.adapter.in.web.dto.CreateUserRequest;
import com.becajava.userservice.adapter.in.web.dto.UpdateUserRequest;
import com.becajava.userservice.adapter.in.web.dto.UserResponse;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.CreateUserUseCase;
import com.becajava.userservice.port.in.GetUserByIdUseCase;
import com.becajava.userservice.port.in.DeleteUserUseCase;
import com.becajava.userservice.port.in.UpdateUserUseCase;
import com.becajava.userservice.port.in.ListUsersUseCase;
import com.becajava.userservice.adapter.in.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Users", description = "Operações de gerenciamento de usuários")
@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário com a senha protegida por BCrypt.")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

        User createdUser = createUserUseCase.createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        UserResponse response = new UserResponse(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
                    summary = "Listar usuários",
                    description = "Retorna todos os usuários cadastrados.",
                    security = @SecurityRequirement(
                    name = OpenApiConfig.BASIC_AUTH
            )
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {

        List<User> users = listUsersUseCase.listUsers();

        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {

            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );

            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }

    @Operation(
                    summary = "Consultar usuário por ID",
                    description = "Retorna o usuário correspondente ao identificador informado.",
                    security = @SecurityRequirement(
                    name = OpenApiConfig.BASIC_AUTH
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = getUserByIdUseCase.getUserById(id);

        UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity.ok(response);
    }

    @Operation(
                    summary = "Atualizar usuário",
                    description = "Atualiza o nome e o e-mail do usuário sem alterar sua senha.",
                    security = @SecurityRequirement(
                    name = OpenApiConfig.BASIC_AUTH
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        User updateUser = updateUserUseCase.updateUser(
                id,
                request.getName(),
                request.getEmail()
        );

        UserResponse response = new UserResponse(
                updateUser.getId(),
                updateUser.getName(),
                updateUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
                    summary = "Excluir usuário",
                    description = "Exclui o usuário correspondente ao identificador informado.",
                    security = @SecurityRequirement(
                    name = OpenApiConfig.BASIC_AUTH
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserUseCase.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
