package com.mgrud.github.proxy.gitproxycore.domain.control;

import com.mgrud.github.proxy.gitproxycore.domain.control.client.GithubApiClient;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubApiClient githubApiClient;
    private final GithubDTOMapper dtoMapper;

    public Collection<GithubRepository> getUserNoForkRepositories(String userName) {
        Collection<GithubRepositoryDTO> repositories = githubApiClient.getUserRepositories(userName).stream()
                .filter(repository -> !repository.getFork())
                .collect(Collectors.toList());

        Map<String, List<GithubBranchWrapper>> branches = githubApiClient.getRepositoryBranches(repositories);

        return repositories.stream()
                .map(repo -> dtoMapper.githubRepository(repo, extractGithubBranches(branches, repo.getName())))
                .collect(Collectors.toList());
    }

    private List<GithubBranchDTO> extractGithubBranches(Map<String, List<GithubBranchWrapper>> branches, String repoName) {
        return branches.getOrDefault(repoName, Collections.emptyList()).stream()
                .map(GithubBranchWrapper::getBranch)
                .collect(Collectors.toList());
    }

}
