package com.mgrud.github.proxy.gitproxycore.domain.control;

import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GithubDTOMapper {

    @Mapping(source = "branches", target = "branches")
    @Mapping(source = "repository.owner.login", target = "ownerLogin")
    GithubRepository githubRepository(GithubRepositoryDTO repository, List<GithubBranchDTO> branches);

    @Mapping(source = "branch.commit.sha", target = "sha")
    GithubBranch githubBranch(GithubBranchDTO branch);

}
