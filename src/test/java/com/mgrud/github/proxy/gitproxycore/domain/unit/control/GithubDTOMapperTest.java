package com.mgrud.github.proxy.gitproxycore.domain.unit.control;

import com.mgrud.github.proxy.gitproxycore.domain.control.GithubDTOMapper;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubCommitDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryOwnerDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubDTOMapperTest {

    private static final String REPOSITORIES_URL_VALUE = "/ForkRepository/branches";
    private static final String BRANCH_URL_VALUE = "/branch/test2";
    private static final String OWNER_LOGIN_VALUE = "TestOwnerLogin";
    private static final String REPOSITORY_NAME = "TestName";
    private static final String BRANCH_NAME_VALUE = "TestBranchName";
    private static final String SHA_VALUE = "TestShaValue";
    private static final GithubDTOMapper mapper = Mappers.getMapper(GithubDTOMapper.class);


    @Test
    public void testGithubRepositoryDTOMappingToGithubRepository() {
        GithubRepositoryDTO repositoryDTO = getGithubRepositoryDTO();
        GithubBranchDTO branchDTO = getGithubBranchDTO();

        GithubRepository entity = mapper.githubRepository(repositoryDTO, Collections.singletonList(branchDTO));
        GithubRepository expectedEntity = getExpectedGithubRepositoryEntity();

        assertThat(entity).isEqualTo(expectedEntity);

    }

    private GithubRepositoryDTO getGithubRepositoryDTO() {
        GithubRepositoryOwnerDTO ownerDTO = new GithubRepositoryOwnerDTO(OWNER_LOGIN_VALUE);
        return new GithubRepositoryDTO(REPOSITORY_NAME, ownerDTO, true, REPOSITORIES_URL_VALUE);
    }

    private GithubBranchDTO getGithubBranchDTO() {
        GithubCommitDTO githubCommitDTO = new GithubCommitDTO(SHA_VALUE, BRANCH_URL_VALUE);
        return new GithubBranchDTO(BRANCH_NAME_VALUE, githubCommitDTO);
    }

    private GithubRepository getExpectedGithubRepositoryEntity() {
        GithubBranch githubBranch = GithubBranch.builder()
                .name(BRANCH_NAME_VALUE)
                .sha(SHA_VALUE)
                .build();

        return GithubRepository.builder()
                .name(REPOSITORY_NAME)
                .ownerLogin(OWNER_LOGIN_VALUE)
                .branches(Collections.singletonList(githubBranch))
                .build();
    }

}
