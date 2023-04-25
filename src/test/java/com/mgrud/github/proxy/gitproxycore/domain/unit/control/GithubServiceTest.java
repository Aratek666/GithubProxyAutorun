package com.mgrud.github.proxy.gitproxycore.domain.unit.control;

import com.mgrud.github.proxy.gitproxycore.domain.control.GithubBranchWrapper;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubDTOMapper;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubDTOMapperImpl;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubService;
import com.mgrud.github.proxy.gitproxycore.domain.control.client.GithubApiClient;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubCommitDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryOwnerDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import com.mgrud.github.proxy.gitproxycore.infrastructure.exception.handler.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class GithubServiceTest {

    private static final String USER_1_REPO_2_BRANCHES_URL_VALUE = "/ForkRepository/branches";
    private static final String USER_1_REPO_1_BRANCHES_URL_VALUE = "/NoForkRepository/branches";
    private static final String USER_1_REPO_1_BRANCH_NAME_VALUE = "User1Repo1BranchName";
    private static final String USER_1_REPO_1_BRANCH_COMMIT_SHA_VALUE = "User1Repo1BranchCommitSha";
    private static final String USER_1_REPO_1_NAME_VALUE = "User1Repo1Name";
    private static final String USER_1_REPO_2_NAME_VALUE = "User1Repo2Name";
    private static final String USER_1_NAME_VALUE = "UserNameWhoHasBothTypeRepos";
    private static final String USER_2_REPO_1_BRANCHES_URL_VALUE = "/ForkRepository/branches";
    private static final String USER_2_REPO_1_NAME_VALUE = "User2Repo1Name";
    private static final String USER_2_NAME_VALUE = "UserNameWhoHasOnlyForkRepo";
    private static final String USER_3_NAME_VALUE = "UserNameWhoHasNoRepo";
    private static final String USER_4_NAME_VALUE = "NotExistingUser";
    @MockBean
    GithubApiClient mockApiClient;
    GithubDTOMapper dtoMapper;
    GithubService githubService;


    @Before
    public void init() {
        dtoMapper = new GithubDTOMapperImpl();
        given(mockApiClient.getUserRepositories(USER_1_NAME_VALUE)).willReturn(getMockUserRepositoriesForUser1());
        given(mockApiClient.getUserRepositories(USER_2_NAME_VALUE)).willReturn(getMockUserRepositoriesForUser2());
        given(mockApiClient.getUserRepositories(USER_3_NAME_VALUE)).willReturn(Collections.emptyList());
        given(mockApiClient.getUserRepositories(USER_4_NAME_VALUE)).willThrow(UserNotFoundException.class);
        given(mockApiClient.getRepositoryBranches(any())).willReturn(getMockRepositoryBranchDTOsForUser1());
        githubService = new GithubService(mockApiClient, dtoMapper);
    }

    @Test
    public void testGetNoForkRepositoriesForUserWhoHasOneForkRepoAndOneNoForkRepo() {
        Collection<GithubRepository> githubRepositories = githubService.getUserNoForkRepositories(USER_1_NAME_VALUE);
        Collection<GithubRepository> expectedRepositories = getExpectedGithubRepositoriesForUser1();

        assertThat(githubRepositories).containsAll(expectedRepositories);
    }

    @Test
    public void testGetNoForkRepositoriesForUserWhoHasOnlyForkRepo() {
        Collection<GithubRepository> githubRepositories = githubService.getUserNoForkRepositories(USER_2_NAME_VALUE);
        assertThat(githubRepositories).isEmpty();
    }

    @Test
    public void testGetNoForkRepositoriesForUserWhoHasNoRepo() {
        Collection<GithubRepository> githubRepositories = githubService.getUserNoForkRepositories(USER_3_NAME_VALUE);
        assertThat(githubRepositories).isEmpty();
    }

    @Test
    public void testGetNoForkRepositoriesForUserWhoIsNotExist() {
        assertThatThrownBy(() -> githubService.getUserNoForkRepositories(USER_4_NAME_VALUE)).isInstanceOf(UserNotFoundException.class);
    }


    private Collection<GithubRepository> getExpectedGithubRepositoriesForUser1() {
        GithubBranch repo1GithubBranch = GithubBranch.builder()
                .name(USER_1_REPO_1_BRANCH_NAME_VALUE)
                .sha(USER_1_REPO_1_BRANCH_COMMIT_SHA_VALUE)
                .build();

        GithubRepository repository1 = GithubRepository.builder()
                .name(USER_1_REPO_1_NAME_VALUE)
                .ownerLogin(USER_1_NAME_VALUE)
                .branches(Collections.singletonList(repo1GithubBranch))
                .build();

        return Collections.singletonList(repository1);
    }

    private Collection<GithubRepositoryDTO> getMockUserRepositoriesForUser1() {
        GithubRepositoryOwnerDTO ownerDTO = new GithubRepositoryOwnerDTO(USER_1_NAME_VALUE);
        GithubRepositoryDTO forkRepository = new GithubRepositoryDTO(USER_1_REPO_2_NAME_VALUE, ownerDTO, true, USER_1_REPO_2_BRANCHES_URL_VALUE);
        GithubRepositoryDTO noForkRepository = new GithubRepositoryDTO(USER_1_REPO_1_NAME_VALUE, ownerDTO, false, USER_1_REPO_1_BRANCHES_URL_VALUE);

        return Arrays.asList(noForkRepository, forkRepository);
    }

    private Map<String, List<GithubBranchWrapper>> getMockRepositoryBranchDTOsForUser1() {
        Map<String, List<GithubBranchWrapper>> branches = new HashMap<>();

        GithubCommitDTO githubCommitDTO1 = new GithubCommitDTO(USER_1_REPO_1_BRANCH_COMMIT_SHA_VALUE, "");
        GithubBranchDTO branch1DTO = new GithubBranchDTO(USER_1_REPO_1_BRANCH_NAME_VALUE, githubCommitDTO1);

        GithubBranchWrapper repositoryWrapper1 = GithubBranchWrapper.builder()
                .branch(branch1DTO)
                .repositoryName(USER_1_REPO_1_NAME_VALUE)
                .build();

        branches.put(USER_1_REPO_1_NAME_VALUE, Collections.singletonList(repositoryWrapper1));
        return branches;
    }

    private Collection<GithubRepositoryDTO> getMockUserRepositoriesForUser2() {
        GithubRepositoryOwnerDTO ownerDTO = new GithubRepositoryOwnerDTO(USER_1_NAME_VALUE);
        GithubRepositoryDTO forkRepository = new GithubRepositoryDTO(USER_2_REPO_1_NAME_VALUE, ownerDTO, true, USER_2_REPO_1_BRANCHES_URL_VALUE);

        return Collections.singletonList(forkRepository);
    }
}
