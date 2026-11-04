package com.becajava.userservice.adapter.in.web.dto;

import java.util.List;

public class ImportUsersResponse {
    private final int totalLines;
    private final int importedUsers;
    private final int failedLines;
    private final List<ImportUserErrorResponse> errors;

    public ImportUsersResponse(int totalLines, int importedUsers, int failedLines, List<ImportUserErrorResponse> errors) {
        this.totalLines = totalLines;
        this.importedUsers = importedUsers;
        this.failedLines = failedLines;
        this.errors = List.copyOf(errors);
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getFailedLines() {
        return failedLines;
    }

    public int getImportedUsers() {
        return importedUsers;
    }

    public List<ImportUserErrorResponse> getErrors() {
        return errors;
    }
}
