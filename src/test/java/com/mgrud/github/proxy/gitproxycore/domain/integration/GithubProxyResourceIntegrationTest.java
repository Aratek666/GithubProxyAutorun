package com.mgrud.github.proxy.gitproxycore.domain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.ErrorResponseDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubProxyBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubCommitDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryOwnerDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-config")
public class GithubProxyResourceIntegrationTest {

    private static final String EXISTING_USER_NAME_WITHOUT_REPOSITORIES = "UserWithNoRepositories";
    private static final String NOT_EXISTING_USER_NAME = "NotExistingUser";
    private static final String EXISTING_USER_NAME = "ExistingUser";
    private static final String REPOSITORIES_URL_VALUE = "/ForkRepository/branches";
    private static final String FORK_REPOSITORY_NAME = "ForkRepositoryTestName";
    private static final String NO_FORK_REPOSITORY_NAME = "NoForkRepositoryTestName";
    private static final String NO_FORK_BRANCH_NAME_VALUE = "NoForkTestBranchName";
    private static final String SHA_VALUE = "TestShaValue";
    private static final String USER_NAME_QUERY_PARAM = "userName";
    private static final String GET_REPOSITORIES_URL = "/user/repositories";
    private static final String GET_USER_REPOSITORIES_GITHUB_API_URL = "/users/%s/repos";
    private static final String GET_USER_REPOSITORIES_BRANCHES_GITHUB_API_URL = "/repos/%s/%s/branches";
    @Autowired
    private MockMvc mvc;

    private WireMockServer wireMockServer;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void init() {
        wireMockServer = new WireMockServer(new WireMockConfiguration().port(8082));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8082);
    }

    @Test
    public void testGetRepositoriesForUserWithoutRepositories() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, EXISTING_USER_NAME_WITHOUT_REPOSITORIES)))
                .willReturn(ok()
                        .withStatus(200)
                        .withJsonBody(objectMapper.valueToTree(Collections.emptyList()))
                        .withHeader("Content-Type", "application/json")));

        this.mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, EXISTING_USER_NAME_WITHOUT_REPOSITORIES))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    public void testGetRepositoriesWithDefinedAcceptHeaderToXML() throws Exception {

        this.mvc.perform(get(GET_REPOSITORIES_URL).header("Accept", "application/xml"))
                .andExpect(status().is(406));
    }

    @Test
    public void testGetRepositoriesWithNotDefinedUserNameParam() throws Exception {

        this.mvc.perform(get(GET_REPOSITORIES_URL))
                .andExpect(status().is(400))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Parameter is missing: userName",
                        ErrorResponseDTO.ErrorCodeEnum.MissingParameter))));
    }

    @Test
    public void testGetRepositoriesForGithubServerException() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, "ServerError")))
                .willReturn(serverError()));

        this.mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, "ServerError"))
                .andExpect(status().is(500))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Occured Github api server exception",
                        ErrorResponseDTO.ErrorCodeEnum.GithubApiError))));
    }

    @Test
    public void testGetRepositoriesForGithubClientException() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, "BadRequest")))
                .willReturn(badRequest()));

        this.mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, "BadRequest"))
                .andExpect(status().is(500))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Occured Github api client exception",
                        ErrorResponseDTO.ErrorCodeEnum.GithubApiError))));
    }

    @Test
    public void testGetRepositoriesForGithubForbiddenClientException() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, "Forbidden")))
                .willReturn(forbidden().withHeader("x-ratelimit-reset", "1000")));

        this.mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, "Forbidden"))
                .andExpect(status().is(500))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("API rate limit exceeded. API will be unlock at: [1000]",
                        ErrorResponseDTO.ErrorCodeEnum.GithubApiError))));
    }

    @Test
    public void testGetRepositoriesForNotExistingUserName() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, NOT_EXISTING_USER_NAME)))
                .willReturn(ok().withStatus(404)));

        this.mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, NOT_EXISTING_USER_NAME))
                .andExpect(status().is(404))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Given user name does not exist",
                        ErrorResponseDTO.ErrorCodeEnum.UserNameNotFound))));
    }

    private ErrorResponseDTO getErrorResponseDTO(String message, ErrorResponseDTO.ErrorCodeEnum errorCodeEnum) {
        return ErrorResponseDTO.builder()
                .message(message)
                .errorCode(errorCodeEnum)
                .build();
    }

    @Test
    public void testGetRepositoriesForUserWithOneForkAndOneNoForkRepository() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, EXISTING_USER_NAME)))
                .willReturn(ok()
                        .withStatus(200)
                        .withJsonBody(objectMapper.valueToTree(getMockGithubRepository()))
                        .withHeader("Content-Type", "application/json")));
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_BRANCHES_GITHUB_API_URL, EXISTING_USER_NAME, NO_FORK_REPOSITORY_NAME)))
                .willReturn(ok()
                        .withStatus(200)
                        .withJsonBody(objectMapper.valueToTree(getMockGithubNoForkRepositoryBranches()))
                        .withHeader("Content-Type", "application/json")));

        this.mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, EXISTING_USER_NAME))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(getResponseExpectedDataForExistingUser())));
    }

    private List<GithubRepositoryDTO> getMockGithubRepository() {
        GithubRepositoryOwnerDTO ownerDTO = new GithubRepositoryOwnerDTO(EXISTING_USER_NAME);
        GithubRepositoryDTO forkRepository = new GithubRepositoryDTO(FORK_REPOSITORY_NAME, ownerDTO, true, REPOSITORIES_URL_VALUE);
        GithubRepositoryDTO noForkRepository = new GithubRepositoryDTO(NO_FORK_REPOSITORY_NAME, ownerDTO, false, REPOSITORIES_URL_VALUE);

        return Arrays.asList(forkRepository, noForkRepository);
    }

    private List<GithubBranchDTO> getMockGithubNoForkRepositoryBranches() {
        GithubCommitDTO githubCommitDTO = new GithubCommitDTO(SHA_VALUE, "");
        GithubBranchDTO branchDTO = new GithubBranchDTO(NO_FORK_BRANCH_NAME_VALUE, githubCommitDTO);

        return Collections.singletonList(branchDTO);
    }

    private List<GithubUserRepositoriesDTO> getResponseExpectedDataForExistingUser() {
        GithubProxyBranchDTO branchDTOS = GithubProxyBranchDTO.builder()
                .name(NO_FORK_BRANCH_NAME_VALUE)
                .sha(SHA_VALUE)
                .build();

        GithubUserRepositoriesDTO dto = GithubUserRepositoriesDTO.builder()
                .name(NO_FORK_REPOSITORY_NAME)
                .ownerLogin(EXISTING_USER_NAME)
                .branches(Collections.singletonList(branchDTOS))
                .build();

        return Collections.singletonList(dto);
    }

    @After
    public void closeWireMockServer() {
        wireMockServer.stop();
    }

}
