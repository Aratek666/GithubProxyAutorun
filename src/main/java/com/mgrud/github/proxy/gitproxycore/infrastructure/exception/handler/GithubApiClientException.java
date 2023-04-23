package com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler;


public class GithubApiClientException extends RuntimeException {
    public GithubApiClientException(String message) {
        super(message);
    }
}