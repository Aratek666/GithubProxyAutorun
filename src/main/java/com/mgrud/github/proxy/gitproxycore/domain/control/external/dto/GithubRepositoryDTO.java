package com.mgrud.github.proxy.gitproxycore.domain.control.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryDTO {
    private String name;
    private GithubRepositoryOwnerDTO owner;
    private Boolean fork;
    private String branches_url;
}