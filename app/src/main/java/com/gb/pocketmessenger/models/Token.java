package com.gb.pocketmessenger.models;

public class Token {
    private String fieldName;
    private String token;

    public Token(String fieldName, String token) {
        this.fieldName = fieldName;
        this.token = token;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
