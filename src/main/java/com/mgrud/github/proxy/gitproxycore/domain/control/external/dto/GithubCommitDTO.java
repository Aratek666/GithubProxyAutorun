package com.mgrud.github.proxy.gitproxycore.domain.control.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubCommitDTO {
    private String sha;
    private String url;
}
