package com.mgrud.github.proxy.gitproxycore.domain.boundary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDTO {
    private ErrorCodeEnum errorCode;
    private String message;
    private int statusCode;


    public enum ErrorCodeEnum {
        UserNameNotFound,
        MissingParameter,
        GithubApiError,
        NotSupportedMediaType
    }
}
