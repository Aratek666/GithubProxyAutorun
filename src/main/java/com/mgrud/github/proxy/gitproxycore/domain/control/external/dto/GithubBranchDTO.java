package com.mgrud.github.proxy.gitproxycore.domain.control.external.dto;

import lombok.Data;

@Data
public class GithubBranchDTO {
    private String name;
    private GithubCommitDTO commit;
}
