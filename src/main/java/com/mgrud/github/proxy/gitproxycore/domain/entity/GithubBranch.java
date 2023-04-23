package com.mgrud.github.proxy.gitproxycore.domain.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GithubBranch {

    private final String name;
    private final String sha;
}
