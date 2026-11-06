package com.becajava.userservice.adapter.in.web.dto;

public class ImportUserErrorResponse {
    private final int line;
    private final String message;

    public ImportUserErrorResponse(int line, String message) {
        this.line = line;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }
}
