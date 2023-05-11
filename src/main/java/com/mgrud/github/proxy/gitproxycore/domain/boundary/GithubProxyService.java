package com.mgrud.github.proxy.gitproxycore.domain.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubService;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubProxyService {

    private final GithubService githubService;
    private final GithubProxyDTOMapper githubProxyDTOMapper;

    public List<GithubUserRepositoriesDTO> getRepositoriesByUserName(String userName) {
        Collection<GithubRepository> userRepositories = githubService.getUserNoForkRepositories(userName);
        return userRepositories.stream()
                .map(githubProxyDTOMapper::githubUserRepositoriesDTO)
                .collect(Collectors.toList());
    }
}
