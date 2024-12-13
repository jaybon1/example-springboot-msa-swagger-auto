package com.example.user.presentation;

public class ResDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public ResDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
