package com.mgrud.github.proxy.gitproxycore.domain.boundary;

import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.ErrorResponseDTO;
import com.mgrud.github.proxy.gitproxycore.domain.boundary.dto.GithubUserRepositoriesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/repositories")
@RequiredArgsConstructor
public class GithubProxyResource {

    private final GithubProxyService resourceService;

    @Operation(summary = "Gets user repositories by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found user repositories",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GithubUserRepositoriesDTO.class)))}),
            @ApiResponse(responseCode = "406", description = "Http Media Type Not Acceptable",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Given user name not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Required param not provided",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))})})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GithubUserRepositoriesDTO> getRepositoriesByUserName(@RequestParam String userName) {
        return resourceService.getRepositoriesByUserName(userName);
    }
}
