package com.mgrud.github.proxy.gitproxycore.domain.boundary.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class GithubUserRepositoriesDTO {
    private final String name;
    private final String ownerLogin;
    private final Collection<GithubProxyBranchDTO> branches;
}
