package com.becajava.userservice.adapter.in.csv;

import com.becajava.userservice.application.exception.InvalidCsvFileException;
import com.becajava.userservice.application.model.ImportUserCommand;
import org.springframework.stereotype.Component;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Component
public class UserCsvParser {

    public List<ImportUserCommand> parse(InputStream inputStream) throws IOException {

        List<ImportUserCommand> commands = new ArrayList<>();

        try (
                BOMInputStream bomInputStream = BOMInputStream.builder()
                        .setInputStream(inputStream)
                        .get();

                Reader reader = new InputStreamReader(
                        bomInputStream,
                        StandardCharsets.UTF_8
                )
        ) {

            CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).get();

            try (CSVParser csvParser = format.parse(reader)) {

                List<String> expectedHeaders = List.of("name", "email", "password");

                List<String> actualHeaders = new ArrayList<>(csvParser.getHeaderMap().keySet());

                if (actualHeaders.isEmpty()) {
                    throw new InvalidCsvFileException("O arquivo CSV está vazio");
                }

                if (!expectedHeaders.equals(actualHeaders)) {
                    throw new InvalidCsvFileException("O cabeçalho do CSV é inválido. Esperado: name,email,password");
                }

                for (CSVRecord record : csvParser) {
                    int lineNumber = Math.toIntExact(record.getRecordNumber() + 1);

                    String name = record.isSet("name") ? record.get("name") : "";
                    String email = record.isSet("email") ? record.get("email") : "";
                    String password = record.isSet("password") ? record.get("password") : "";

                    ImportUserCommand command = new ImportUserCommand(lineNumber, name, email, password);

                    commands.add(command);
                }
            }
        }

        if (commands.isEmpty()) {
            throw new InvalidCsvFileException("O arquivo CSV não contém usuários");
        }

        return commands;
    }
}
