package com.harrisburg.app.exception;

public class InvalidUserCredentialException extends RuntimeException {

    public InvalidUserCredentialException() {
        super();
    }

    public InvalidUserCredentialException(String message) {
        super(message);
    }
}
