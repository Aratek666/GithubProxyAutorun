package com.mgrud.github.proxy.gitproxycore.domain.unit.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyResource;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyService;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubProxyBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class GithubProxyResourceTest {

    private static final String OWNER_LOGIN_VALUE = "TestOwnerLogin";
    private static final String REPOSITORY_NAME = "TestName";
    private static final String BRANCH_NAME_VALUE = "TestBranchName";
    private static final String SHA_VALUE = "TestShaValue";

    @MockBean
    private GithubProxyService githubProxyService;
    private GithubProxyResource githubProxyResource;

    @Before
    public void init() {
        given(githubProxyService.getRepositoriesByUserName(any())).willReturn(getMockUserRepositories());
        githubProxyResource = new GithubProxyResource(githubProxyService);
    }

    @Test
    public void testGetUserRepositories() {
        Collection<GithubUserRepositoriesDTO> repositoriesDTOs = githubProxyService.getRepositoriesByUserName("UserName");
        Collection<GithubUserRepositoriesDTO> exptectedRepositoriesDTOs = getExpectedGithubUserRepositoriesDTOValue();

        assertThat(repositoriesDTOs).containsAll(exptectedRepositoriesDTOs);
    }


    private List<GithubUserRepositoriesDTO> getMockUserRepositories() {
        GithubProxyBranchDTO githubBranch = GithubProxyBranchDTO.builder()
                .name(BRANCH_NAME_VALUE)
                .sha(SHA_VALUE)
                .build();

        GithubUserRepositoriesDTO repository = GithubUserRepositoriesDTO.builder()
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
