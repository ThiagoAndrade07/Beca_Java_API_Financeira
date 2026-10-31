package com.becajava.userservice.adapter.in.web.dto;

import java.util.Map;

public class ApiErrorResponse {

    private int status;
    private String message;
    private Map<String, String> erros;

    public ApiErrorResponse(int status, String message, Map<String, String> erros) {
        this.status = status;
        this.message = message;
        this.erros = erros;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErros() {
        return erros;
    }
}
