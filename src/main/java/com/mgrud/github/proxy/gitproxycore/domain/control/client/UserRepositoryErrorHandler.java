package com.mgrud.github.proxy.gitproxycore.domain.control.client;

import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.GithubApiClientException;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.GithubApiServerException;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
public class UserRepositoryErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UserNotFoundException("Given user name does not exist");
        }
        if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new GithubApiClientException("API rate limit exceeded. API will be unlock at: " + response.getHeaders().get("x-ratelimit-reset"));
        }
        if (response.getStatusCode().is4xxClientError()) {
            throw new GithubApiClientException("Occured Github api client exception");
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new GithubApiServerException("Occured Github api server exception");
        } else {
            super.handleError(response);
        }
    }
}