package com.mgrud.github.proxy.gitproxycore.domain.unit.data;

import com.mgrud.github.proxy.gitproxycore.domain.control.GithubBranchWrapper;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubCommitDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryOwnerDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;

import java.util.*;

public class TestDataPreparator {

    public static Collection<GithubRepositoryDTO> getUserRepositories() {
        Collection<GithubRepositoryDTO> repositoriesDTOs = new ArrayList<>();

        GithubRepositoryOwnerDTO ownerDTO = new GithubRepositoryOwnerDTO("TestUserName");

        GithubRepositoryDTO forkRepository = new GithubRepositoryDTO("ForkRepository", ownerDTO, true, "/ForkRepository/branches");
        repositoriesDTOs.add(forkRepository);

        GithubRepositoryDTO noForkRepository = new GithubRepositoryDTO("NoForkRepository", ownerDTO, false, "/NoForkRepository/branches");
        repositoriesDTOs.add(noForkRepository);

        return repositoriesDTOs;
    }

    public static Map<String, List<GithubBranchWrapper>> getRepositoryBranchDTOs() {
        Map<String, List<GithubBranchWrapper>> branches = new HashMap<>();
        GithubCommitDTO githubCommitDTO1 = new GithubCommitDTO("TestSha1", "/branch/test1");

        GithubBranchDTO branch1DTO = new GithubBranchDTO("TestBranchName1", githubCommitDTO1);

        GithubBranchWrapper repositoryWrapper1 = GithubBranchWrapper.builder()
                .branch(branch1DTO)
                .repositoryName("NoForkRepository")
                .build();

        branches.put("NoForkRepository", Collections.singletonList(repositoryWrapper1));

        GithubCommitDTO githubCommitDTO2 = new GithubCommitDTO("TestSha2", "/branch/test2");

        GithubBranchDTO branch2DTO = new GithubBranchDTO("TestBranchName2", githubCommitDTO2);

        GithubBranchWrapper repositoryWrapper2 = GithubBranchWrapper.builder()
                .branch(branch2DTO)
                .repositoryName("ForkRepository")
                .build();

        branches.put("ForkRepository", Collections.singletonList(repositoryWrapper2));

        return branches;
    }


    public static GithubRepository getGithubRepositoryEntity() {
        GithubBranch githubBranch = GithubBranch.builder()
                .name("TestBranchName")
                .sha("TestShaValue")
                .build();

        return GithubRepository.builder()
                .name("TestName")
                .ownerLogin("TestOwnerLogin")
                .branches(Collections.singleton(githubBranch))
                .build();
    }

}
