package com.example.exception;

public class TinyApiException extends RuntimeException {
    public TinyApiException(String message) {
        super(message);
    }

    public TinyApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
