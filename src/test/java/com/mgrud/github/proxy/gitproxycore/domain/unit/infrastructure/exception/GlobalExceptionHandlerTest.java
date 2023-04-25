package com.mgrud.github.proxy.gitproxycore.domain.unit.infrastructure.exception;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.ErrorResponseDTO;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.GithubApiClientException;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.GithubApiServerException;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.GlobalExceptionHandler;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.UserNotFoundException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;


public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleUserNotFoundException() {
        UserNotFoundException userNotFoundException = new UserNotFoundException("User not found");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleUserNotFoundException(userNotFoundException);

        assertThat(response).isEqualTo(getExpectedResponseValueForUserNotFoundException(userNotFoundException));
    }

    private ResponseEntity<ErrorResponseDTO> getExpectedResponseValueForUserNotFoundException(UserNotFoundException userNotFoundException) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.UserNameNotFound)
                .message(userNotFoundException.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @Test
    public void testHandleGithubClientException() {
        GithubApiClientException githubApiClientException = new GithubApiClientException("Occured Github api client exception");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGithubClientException(githubApiClientException);

        assertThat(response).isEqualTo(getExpectedResponseValueForGithubApiClientException(githubApiClientException));
    }

    private ResponseEntity<ErrorResponseDTO> getExpectedResponseValueForGithubApiClientException(GithubApiClientException githubApiClientException) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.GithubApiError)
                .message(githubApiClientException.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @Test
    public void testHandleGithubServerException() {
        GithubApiServerException githubApiServerException = new GithubApiServerException("Occured Github api server exception");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGithubClientException(githubApiServerException);

        assertThat(response).isEqualTo(getExpectedResponseValueForGithubApiServerException(githubApiServerException));
    }

    private ResponseEntity<ErrorResponseDTO> getExpectedResponseValueForGithubApiServerException(GithubApiServerException githubApiServerException) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.GithubApiError)
                .message(githubApiServerException.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


    @Test
    public void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException missingServletRequestParameterException = new MissingServletRequestParameterException("userName", "String");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleMissingServletRequestParameterException(missingServletRequestParameterException);

        assertThat(response).isEqualTo(getExpectedResponseValueForMissingServletRequestParameterException(missingServletRequestParameterException));
    }

    private ResponseEntity<ErrorResponseDTO> getExpectedResponseValueForMissingServletRequestParameterException(
            MissingServletRequestParameterException missingParameterException) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.MissingParameter)
                .message("Parameter is missing: " + missingParameterException.getParameterName())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
