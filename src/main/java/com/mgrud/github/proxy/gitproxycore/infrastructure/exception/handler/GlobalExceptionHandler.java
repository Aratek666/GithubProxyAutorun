package com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.UserNameNotFound)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(GithubApiClientException.class)
    public ResponseEntity<ErrorResponseDTO> handleGithubClientException(GithubApiClientException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.GithubApiError)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(GithubApiServerException.class)
    public ResponseEntity<ErrorResponseDTO> handleGithubClientException(GithubApiServerException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.GithubApiError)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .errorCode(ErrorResponseDTO.ErrorCodeEnum.MissingParameter)
                .message("Parameter is missing: " + ex.getParameterName())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}