package com.mgrud.github.proxy.gitproxycore.domain.control.external.dto;

import lombok.Data;

@Data
public class GithubRepositoryDTO {
    private String name;
    private GithubRepositoryOwnerDTO owner;
    private Boolean fork;
    private String branches_url;
}