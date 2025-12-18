package com.example.exception;

public class TinyRateLimitException extends RuntimeException {
    public TinyRateLimitException(String message) {
        super(message);
    }
}
