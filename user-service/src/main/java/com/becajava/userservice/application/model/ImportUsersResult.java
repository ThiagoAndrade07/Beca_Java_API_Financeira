package com.becajava.userservice.application.model;

import java.util.List;


public class ImportUsersResult {

    private final int totalLines;
    private final int importedUsers;
    private final List<ImportUserError> errors;

    public ImportUsersResult(int totalLines, int importedUsers, List<ImportUserError> errors) {
        this.totalLines = totalLines;
        this.importedUsers = importedUsers;
        this.errors = List.copyOf(errors);
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getImportedUsers() {
        return importedUsers;
    }

    public List<ImportUserError> getErrors() {
        return errors;
    }

    public int getFailedLines() {
        return errors.size();
    }
}
