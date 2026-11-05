package com.becajava.userservice.adapter.in.csv;

import com.becajava.userservice.application.exception.InvalidCsvFileException;
import com.becajava.userservice.application.model.ImportUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserCsvParserTest {

    private UserCsvParser userCsvParser;

    @BeforeEach
    void setUp() {
        userCsvParser = new UserCsvParser();
    }

    @Test
    void shouldParseValidCsv() {
        String csvContent = """
                name,email,password
                Maria Silva,maria@example.com,Senha123
                João Souza,joao@example.com,OutraSenha456
                """;

        InputStream inputStream = createInputStream(csvContent);

        List<ImportUserCommand> commands = userCsvParser.parse(inputStream);

        assertEquals(2, commands.size());

        ImportUserCommand firstCommand = commands.get(0);

        assertEquals(2, firstCommand.getLineNumber());
        assertEquals("Maria Silva", firstCommand.getName());
        assertEquals("maria@example.com", firstCommand.getEmail());
        assertEquals("Senha123", firstCommand.getPassword());

        ImportUserCommand secondCommand = commands.get(1);

        assertEquals(3, secondCommand.getLineNumber());
        assertEquals("João Souza", secondCommand.getName());
        assertEquals("joao@example.com", secondCommand.getEmail());
        assertEquals("OutraSenha456", secondCommand.getPassword());
    }

    @Test
    void shouldAcceptCsvWithUtf8Bom() {
        String csvContent = "\uFEFF" + """
                name,email,password
                Maria Silva,maria@example.com,Senha123
                """;

        List<ImportUserCommand> commands =
                userCsvParser.parse(createInputStream(csvContent));

        assertEquals(1, commands.size());
        assertEquals("Maria Silva", commands.get(0).getName());
    }

    @Test
    void shouldRejectInvalidHeader() {
        String csvContent = """
                nome,email,senha
                Maria Silva,maria@example.com,Senha123
                """;

        InvalidCsvFileException exception = assertThrows(
                InvalidCsvFileException.class,
                () -> userCsvParser.parse(createInputStream(csvContent))
        );

        assertEquals(
                "O cabeçalho do CSV é inválido. Esperado: name,email,password",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectCsvWithoutUsers() {
        String csvContent = """
                name,email,password
                """;

        InvalidCsvFileException exception = assertThrows(
                InvalidCsvFileException.class,
                () -> userCsvParser.parse(createInputStream(csvContent))
        );

        assertEquals(
                "O arquivo CSV não contém usuários",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectMalformedCsv() {
        String csvContent = """
                name,email,password
                "Maria Silva,maria@example.com,Senha123
                """;

        InvalidCsvFileException exception = assertThrows(
                InvalidCsvFileException.class,
                () -> userCsvParser.parse(createInputStream(csvContent))
        );

        assertEquals(
                "O arquivo CSV não pôde ser processado",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectCsvWithMoreThanOneThousandUsers() {
        StringBuilder csvContent = new StringBuilder(
                "name,email,password\n"
        );

        for (int number = 1; number <= 1001; number++) {
            csvContent.append("Usuario ")
                    .append(number)
                    .append(",usuario")
                    .append(number)
                    .append("@example.com,Senha123\n");
        }

        InvalidCsvFileException exception = assertThrows(
                InvalidCsvFileException.class,
                () -> userCsvParser.parse(
                        createInputStream(csvContent.toString())
                )
        );

        assertEquals(
                "O arquivo CSV deve conter no máximo 1000 linhas de usuários",
                exception.getMessage()
        );
    }

    private InputStream createInputStream(String content) {
        return new ByteArrayInputStream(
                content.getBytes(StandardCharsets.UTF_8)
        );
    }
}