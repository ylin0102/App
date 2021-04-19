package com.harrisburg.app.exception;

public class UserExistedException extends RuntimeException {

    public UserExistedException() {
        super();
    }

    public UserExistedException(String message) {
        super(message);
    }
}
