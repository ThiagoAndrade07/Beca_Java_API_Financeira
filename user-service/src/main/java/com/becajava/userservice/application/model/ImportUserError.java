package com.becajava.userservice.application.model;

public class ImportUserError {

    private final int lineNumber;
    private final String message;

    public ImportUserError(int lineNumber, String message) {
        this.lineNumber = lineNumber;
        this.message = message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }
}
