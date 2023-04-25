package com.mgrud.github.proxy.gitproxycore.domain.unit.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyDTOMapper;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubProxyBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubProxyDTOMapperTest {
    private static final String OWNER_LOGIN_VALUE = "TestOwnerLogin";
    private static final String REPOSITORY_NAME = "TestName";
    private static final String BRANCH_NAME_VALUE = "TestBranchName";
    private static final String SHA_VALUE = "TestShaValue";
    private static final GithubProxyDTOMapper mapper = Mappers.getMapper(GithubProxyDTOMapper.class);


    @Test
    public void testGithubRepositoryMappingToGithubUserRepositoriesDTO() {
        GithubRepository entity = getGithubRepositoryEntity();
        GithubUserRepositoriesDTO dto = mapper.githubUserRepositoriesDTO(entity);
        GithubUserRepositoriesDTO expectedValue = getExpectedGithubUserRepositoriesDTOValue();

        assertThat(dto).isEqualTo(expectedValue);
    }


    private GithubRepository getGithubRepositoryEntity() {
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

    private GithubUserRepositoriesDTO getExpectedGithubUserRepositoriesDTOValue() {
        GithubProxyBranchDTO branchDTOS = GithubProxyBranchDTO.builder()
                .name(BRANCH_NAME_VALUE)
                .sha(SHA_VALUE)
                .build();

        return GithubUserRepositoriesDTO.builder()
                .name(REPOSITORY_NAME)
                .ownerLogin(OWNER_LOGIN_VALUE)
                .branches(Collections.singletonList(branchDTOS))
                .build();
    }


}
