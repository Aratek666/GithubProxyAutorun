package com.mgrud.github.proxy.gitproxycore.domain.control.external.dto;

import lombok.Data;

@Data
public class GithubCommitDTO {
    private String sha;
    private String url;
}
