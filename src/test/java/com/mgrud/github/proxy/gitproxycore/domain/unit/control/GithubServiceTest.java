package com.mgrud.github.proxy.gitproxycore.domain.unit.control;

import com.mgrud.github.proxy.gitproxycore.domain.control.GithubDTOMapper;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubDTOMapperImpl;
import com.mgrud.github.proxy.gitproxycore.domain.control.GithubService;
import com.mgrud.github.proxy.gitproxycore.domain.control.client.GithubApiClient;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import com.mgrud.github.proxy.gitproxycore.domain.unit.data.TestDataPreparator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class GithubServiceTest {

    @MockBean
    GithubApiClient mockApiClient;
    GithubDTOMapper dtoMapper;
    GithubService githubService;


    @Before
    public void init() {
        dtoMapper = new GithubDTOMapperImpl();
        given(mockApiClient.getUserRepositories("TestUserName")).willReturn(TestDataPreparator.getUserRepositories());
        given(mockApiClient.getRepositoryBranches(any())).willReturn(TestDataPreparator.getRepositoryBranchDTOs());
        githubService = new GithubService(mockApiClient, dtoMapper);
    }

    @Test
    public void testGetNoForkRepositories() {
        Collection<GithubRepository> githubRepositories = githubService.getUserNoForkRepositories("TestUserName");
        assertEquals(githubRepositories.size(), 1);

        GithubRepository repository = githubRepositories.stream().findFirst().get();
        assertEquals(repository.getName(), "NoForkRepository");
        assertEquals(repository.getOwnerLogin(), "TestUserName");
        assertEquals(repository.getBranches().size(), 1);
        assertTrue(isBranchWithNameExistAtRepository(repository.getBranches(), "TestBranchName1"));
    }

    private boolean isBranchWithNameExistAtRepository(Collection<GithubBranch> branches, String branchName) {
        return branches.stream()
                .anyMatch(branch -> branch.getName().equals(branchName));
    }


}
