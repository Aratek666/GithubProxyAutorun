package com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler;


public class GithubApiServerException extends RuntimeException {
    public GithubApiServerException(String message) {
        super(message);
    }
}