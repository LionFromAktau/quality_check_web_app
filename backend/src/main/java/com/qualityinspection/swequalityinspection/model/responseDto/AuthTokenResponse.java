package com.qualityinspection.swequalityinspection.model.responseDto;

public class AuthTokenResponse {
    private String token;
    private String message;

    //Authentication Token
    public AuthTokenResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}