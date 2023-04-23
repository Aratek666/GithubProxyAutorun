package com.mgrud.github.proxy.gitproxycore.domain.boundary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubProxyBranchDTO {
    private final String name;
    private final String sha;
}
