package com.mgrud.github.proxy.gitproxycore.domain.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubProxyBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GithubProxyDTOMapper {
    GithubUserRepositoriesDTO githubUserRepositoriesDTO(GithubRepository repository);

    GithubRepository githubRepository(GithubUserRepositoriesDTO repository);

    GithubBranch githubBranch(GithubProxyBranchDTO branch);

    GithubProxyBranchDTO githubBranchProxyDTO(GithubBranch branch);
}
