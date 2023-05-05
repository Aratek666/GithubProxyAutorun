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
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWireMock(port = 0)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

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

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void initWireMock() {
        wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        wireMockServer.start();
    }

    @Test
    public void testGetRepositoriesForUserWithoutRepositories() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, EXISTING_USER_NAME_WITHOUT_REPOSITORIES)))
                .willReturn(ok()
                        .withJsonBody(objectMapper.valueToTree(Collections.emptyList()))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, EXISTING_USER_NAME_WITHOUT_REPOSITORIES))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    public void testGetRepositoriesWithDefinedAcceptHeaderToXML() throws Exception {
        mvc.perform(get(GET_REPOSITORIES_URL).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Only following media types: [application/json] are supported",
                        ErrorResponseDTO.ErrorCodeEnum.NotSupportedMediaType, HttpStatus.NOT_ACCEPTABLE.value()))));
    }

    @Test
    public void testGetRepositoriesWithNotDefinedUserNameParam() throws Exception {

        mvc.perform(get(GET_REPOSITORIES_URL))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Parameter is missing: userName",
                        ErrorResponseDTO.ErrorCodeEnum.MissingParameter, HttpStatus.BAD_REQUEST.value()))));
    }

    @Test
    public void testGetRepositoriesForGithubServerException() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, "ServerError")))
                .willReturn(serverError()));

        mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, "ServerError"))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Occured Github api server exception",
                        ErrorResponseDTO.ErrorCodeEnum.GithubApiError, HttpStatus.INTERNAL_SERVER_ERROR.value()))));
    }

    @Test
    public void testGetRepositoriesForGithubClientException() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, "BadRequest")))
                .willReturn(badRequest()));

        mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, "BadRequest"))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Occured Github api client exception",
                        ErrorResponseDTO.ErrorCodeEnum.GithubApiError, HttpStatus.INTERNAL_SERVER_ERROR.value()))));
    }

    @Test
    public void testGetRepositoriesForGithubForbiddenClientException() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, "Forbidden")))
                .willReturn(forbidden().withHeader("x-ratelimit-reset", "1000")));

        mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, "Forbidden"))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("API rate limit exceeded. API will be unlock at: [1000]",
                        ErrorResponseDTO.ErrorCodeEnum.GithubApiError, HttpStatus.INTERNAL_SERVER_ERROR.value()))));
    }

    @Test
    public void testGetRepositoriesForNotExistingUserName() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, NOT_EXISTING_USER_NAME)))
                .willReturn(ok().withStatus(HttpStatus.NOT_FOUND.value())));

        mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, NOT_EXISTING_USER_NAME))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(getErrorResponseDTO("Given user name does not exist",
                        ErrorResponseDTO.ErrorCodeEnum.UserNameNotFound, HttpStatus.NOT_FOUND.value()))));
    }

    private ErrorResponseDTO getErrorResponseDTO(String message, ErrorResponseDTO.ErrorCodeEnum errorCodeEnum, int statusCode) {
        return ErrorResponseDTO.builder()
                .message(message)
                .errorCode(errorCodeEnum)
                .statusCode(statusCode)
                .build();
    }

    @Test
    public void testGetRepositoriesForUserWithOneForkAndOneNoForkRepository() throws Exception {
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_GITHUB_API_URL, EXISTING_USER_NAME)))
                .willReturn(ok()
                        .withJsonBody(objectMapper.valueToTree(getMockGithubRepository()))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
        stubFor(WireMock.get(urlEqualTo(String.format(GET_USER_REPOSITORIES_BRANCHES_GITHUB_API_URL, EXISTING_USER_NAME, NO_FORK_REPOSITORY_NAME)))
                .willReturn(ok()
                        .withJsonBody(objectMapper.valueToTree(getMockGithubNoForkRepositoryBranches()))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        mvc.perform(get(GET_REPOSITORIES_URL).queryParam(USER_NAME_QUERY_PARAM, EXISTING_USER_NAME))
                .andExpect(status().is(HttpStatus.OK.value()))
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

