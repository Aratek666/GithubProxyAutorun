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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GithubDTOMapperTest {
    private final GithubDTOMapper mapper = Mappers.getMapper(GithubDTOMapper.class);


    @Test
    public void testGithubRepositoryDTOMappingToGithubRepository() {
        GithubRepositoryOwnerDTO ownerDTO = new GithubRepositoryOwnerDTO("TestUserName");
        GithubRepositoryDTO repositoryDTO = new GithubRepositoryDTO("ForkRepository", ownerDTO, true, "/ForkRepository/branches");
        GithubCommitDTO githubCommitDTO = new GithubCommitDTO("TestSha2", "/branch/test2");
        GithubBranchDTO branchDTO = new GithubBranchDTO("TestBranchName2", githubCommitDTO);
        GithubRepository entity = mapper.githubRepository(repositoryDTO, Collections.singletonList(branchDTO));

        assertEquals(repositoryDTO.getName(), entity.getName());
        assertEquals(repositoryDTO.getOwner().getLogin(), entity.getOwnerLogin());
        assertEquals(entity.getBranches().size(), 1);

        Optional<GithubBranch> entityBranch = entity.getBranches().stream()
                .filter(branch -> branch.getName().equals(branchDTO.getName()))
                .findFirst();

        assertEquals(entityBranch.get().getSha(), branchDTO.getCommit().getSha());
    }

}
