package com.it.exception;

public class InvalidMemberException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidMemberException() {
        super();
    }

    public InvalidMemberException(String message) {
        super(message);
    }
}
