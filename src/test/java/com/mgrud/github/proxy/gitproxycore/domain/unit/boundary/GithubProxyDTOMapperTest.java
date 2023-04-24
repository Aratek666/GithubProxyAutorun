package com.mgrud.github.proxy.gitproxycore.domain.unit.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.GithubProxyDTOMapper;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubProxyBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubBranch;
import com.mgrud.github.proxy.gitproxycore.domain.entity.GithubRepository;
import com.mgrud.github.proxy.gitproxycore.domain.unit.data.TestDataPreparator;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GithubProxyDTOMapperTest {
    private final GithubProxyDTOMapper mapper = Mappers.getMapper(GithubProxyDTOMapper.class);


    @Test
    public void testGithubRepositoryMappingToGithubUserRepositoriesDTO() {
        GithubRepository entity = TestDataPreparator.getGithubRepositoryEntity();
        GithubUserRepositoriesDTO dto = mapper.githubUserRepositoriesDTO(entity);

        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getOwnerLogin(), entity.getOwnerLogin());
        assertEquals(dto.getBranches().size(), entity.getBranches().size());

        dto.getBranches().forEach(branch -> assertBranch(entity.getBranches(), branch));
    }

    private void assertBranch(Collection<GithubBranch> entityBranches, GithubProxyBranchDTO dto) {
        Optional<GithubBranch> entityBranch = entityBranches.stream()
                .filter(branch -> branch.getName().equals(dto.getName()))
                .findFirst();

        assertEquals(entityBranch.get().getSha(), dto.getSha());
    }


}
