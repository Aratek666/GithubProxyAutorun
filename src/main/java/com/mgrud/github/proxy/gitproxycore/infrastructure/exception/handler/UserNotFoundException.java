package com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}