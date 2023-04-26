package com.mgrud.github.proxy.gitproxycore.domain.unit.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyDTOMapper;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyDTOMapperImpl;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyService;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubProxyBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubService;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class GithubProxyServiceTest {

    private static final String OWNER_LOGIN_VALUE = "TestOwnerLogin";
    private static final String REPOSITORY_NAME = "TestName";
    private static final String BRANCH_NAME_VALUE = "TestBranchName";
    private static final String SHA_VALUE = "TestShaValue";

    private final GithubService githubService = Mockito.mock(GithubService.class);
    private final GithubProxyDTOMapper githubProxyDTOMapper = new GithubProxyDTOMapperImpl();
    private final GithubProxyService githubProxyService = new GithubProxyService(githubService, githubProxyDTOMapper);

    @Test
    public void testGetUserRepositories() {
        given(githubService.getUserNoForkRepositories(any())).willReturn(getMockUserRepositories());

        Collection<GithubUserRepositoriesDTO> repositoriesDTOs = githubProxyService.getRepositoriesByUserName("UserName");
        Collection<GithubUserRepositoriesDTO> exptectedRepositoriesDTOs = getExpectedGithubUserRepositoriesDTOValue();

        assertThat(repositoriesDTOs).containsAll(exptectedRepositoriesDTOs);
    }


    private Collection<GithubRepository> getMockUserRepositories() {
        GithubBranch githubBranch = GithubBranch.builder()
                .name(BRANCH_NAME_VALUE)
                .sha(SHA_VALUE)
                .build();

        GithubRepository repository = GithubRepository.builder()
                .name(REPOSITORY_NAME)
                .ownerLogin(OWNER_LOGIN_VALUE)
                .branches(Collections.singletonList(githubBranch))
                .build();

        return Collections.singletonList(repository);
    }

    private Collection<GithubUserRepositoriesDTO> getExpectedGithubUserRepositoriesDTOValue() {
        GithubProxyBranchDTO branchDTOS = GithubProxyBranchDTO.builder()
                .name(BRANCH_NAME_VALUE)
                .sha(SHA_VALUE)
                .build();

        GithubUserRepositoriesDTO dto = GithubUserRepositoriesDTO.builder()
                .name(REPOSITORY_NAME)
                .ownerLogin(OWNER_LOGIN_VALUE)
                .branches(Collections.singletonList(branchDTOS))
                .build();

        return Collections.singletonList(dto);
    }

}
