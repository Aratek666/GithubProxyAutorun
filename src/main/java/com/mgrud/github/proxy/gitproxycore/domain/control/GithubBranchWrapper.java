package com.mgrud.github.proxy.gitproxycore.domain.control;

import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubBranchWrapper {
    private final String repositoryName;
    private final GithubBranchDTO branch;
}
