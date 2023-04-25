package com.mgrud.github.proxy.gitproxycore.domain.control.client;

import com.mgrud.github.proxy.gitproxycore.domain.control.GithubBranchWrapper;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO;
import com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubRepositoryDTO;
import com.mgrud.github.proxy.gitproxycore.infrastructure.thread.AsynchronousQueryUtil;
import com.mgrud.github.proxy.gitproxycore.infrastructure.thread.ThreadPoolService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GithubApiClient {
    @Value("${base.url.github}")
    private String GITHUB_BASE_URL;
    private static final String GITHUB_GET_USER_REPOSITORIES_URL = "/users/%s/repos";
    private static final String GITHUB_GET_USER_REPOSITORIES_BRANCHES_URL = "/repos/%s/%s/branches";


    private final RestTemplate restTemplate;
    private final ThreadPoolService threadPoolService;
    private final UserRepositoryErrorHandler userRepositoryErrorHandler;

    public GithubApiClient(RestTemplateBuilder restTemplateBuilder, ThreadPoolService threadPoolService, UserRepositoryErrorHandler userRepositoryErrorHandler) {
        this.restTemplate = restTemplateBuilder.build();
        this.userRepositoryErrorHandler = userRepositoryErrorHandler;
        this.threadPoolService = threadPoolService;
    }

    public Collection<GithubRepositoryDTO> getUserRepositories(String userName) {
        restTemplate.setErrorHandler(userRepositoryErrorHandler);
        return Arrays.asList(restTemplate.getForObject(String.format(GITHUB_BASE_URL + GITHUB_GET_USER_REPOSITORIES_URL, userName), GithubRepositoryDTO[].class));
    }

    // Sorry for this comment (gonna remove it later) but i didnt know how to make contact with you, hope you gonna read that.
    /* Referring to your Code Review comment " data is downloaded sequentially, not in parallel" : The following method performs parallel requests for
     branches for given repositories in different threads, by using CompletableFuture.supplyAsync(). I could just use here Parallel stream which would be much more
     easier to use rather than CompletableFuture, but I tend to use the second solution at my current job to be able to manage the pool of available threads.
     If you want I can change that but at this moment it works fine, here are some example logs for query branches for three different repos:
     2023-04-25T15:44:21.491+02:00 DEBUG 21148 --- [y-core-thread-1] o.s.web.client.RestTemplate              : HTTP GET https://api.github.com/repos/Aratek666/GtihubProxyTestRepo/branches
     2023-04-25T15:44:21.491+02:00 DEBUG 21148 --- [y-core-thread-2] o.s.web.client.RestTemplate              : HTTP GET https://api.github.com/repos/Aratek666/TestAutoRUN/branches
     2023-04-25T15:44:21.491+02:00 DEBUG 21148 --- [y-core-thread-0] o.s.web.client.RestTemplate              : HTTP GET https://api.github.com/repos/Aratek666/GithubProxyAutorun/branches
     2023-04-25T15:44:21.491+02:00 DEBUG 21148 --- [y-core-thread-0] o.s.web.client.RestTemplate              : Accept=[application/json, application/*+json]
     2023-04-25T15:44:21.491+02:00 DEBUG 21148 --- [y-core-thread-1] o.s.web.client.RestTemplate              : Accept=[application/json, application/*+json]
     2023-04-25T15:44:21.491+02:00 DEBUG 21148 --- [y-core-thread-2] o.s.web.client.RestTemplate              : Accept=[application/json, application/*+json]
     2023-04-25T15:44:21.708+02:00 DEBUG 21148 --- [y-core-thread-2] o.s.web.client.RestTemplate              : Response 200 OK
     2023-04-25T15:44:21.708+02:00 DEBUG 21148 --- [y-core-thread-2] o.s.web.client.RestTemplate              : Reading to [com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO[]]
     2023-04-25T15:44:21.923+02:00 DEBUG 21148 --- [y-core-thread-0] o.s.web.client.RestTemplate              : Response 200 OK
     2023-04-25T15:44:21.923+02:00 DEBUG 21148 --- [y-core-thread-0] o.s.web.client.RestTemplate              : Reading to [com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO[]]
     2023-04-25T15:44:21.994+02:00 DEBUG 21148 --- [y-core-thread-1] o.s.web.client.RestTemplate              : Response 200 OK
     2023-04-25T15:44:21.994+02:00 DEBUG 21148 --- [y-core-thread-1] o.s.web.client.RestTemplate              : Reading to [com.mgrud.github.proxy.gitproxycore.domain.control.external.dto.GithubBranchDTO[]]
     */

    public Map<String, List<GithubBranchWrapper>> getRepositoryBranches(Collection<GithubRepositoryDTO> repositoryDTOs) {

        Collection<CompletableFuture<Collection<GithubBranchWrapper>>> result = repositoryDTOs.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> getRepositoryBranches(url), threadPoolService.getExecutorService()))
                .collect(Collectors.toList());

        return AsynchronousQueryUtil.getFutureQueriesResult(result, "Could not query repositories branches").stream()
                .collect(Collectors.groupingBy(GithubBranchWrapper::getRepositoryName, Collectors.toList()));
    }

    private Collection<GithubBranchWrapper> getRepositoryBranches(GithubRepositoryDTO repositoryDTO) {
        String branchesURL = String.format(
                GITHUB_BASE_URL + GITHUB_GET_USER_REPOSITORIES_BRANCHES_URL, repositoryDTO.getOwner().getLogin(), repositoryDTO.getName());
        Collection<GithubBranchDTO> branches = Arrays.asList(restTemplate.getForObject(branchesURL, GithubBranchDTO[].class));

        return branches.stream()
                .map(branch -> mapToGithubBranchWrapper(branch, repositoryDTO.getName()))
                .collect(Collectors.toList());
    }

    private GithubBranchWrapper mapToGithubBranchWrapper(GithubBranchDTO branchDTO, String repositoryName) {
        return GithubBranchWrapper.builder()
                .branch(branchDTO)
                .repositoryName(repositoryName)
                .build();
    }


}