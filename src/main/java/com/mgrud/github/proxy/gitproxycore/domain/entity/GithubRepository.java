package com.mgrud.github.proxy.gitproxycore.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Builder
@Data
public class GithubRepository {

    private final String name;
    private final String ownerLogin;
    private final Collection<GithubBranch> branches;

}
