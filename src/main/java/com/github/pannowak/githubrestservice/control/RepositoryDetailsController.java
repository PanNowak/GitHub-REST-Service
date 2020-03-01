package com.github.pannowak.githubrestservice.control;

import com.github.pannowak.githubrestservice.entity.RepoRequestParams;
import com.github.pannowak.githubrestservice.entity.RepositoryDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/repositories")
public class RepositoryDetailsController {

    private final GitHubClient gitHubClient;

    @Autowired
    RepositoryDetailsController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    @GetMapping(value = "/{owner}/{repository-name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RepositoryDetails> getResponse(@PathVariable("owner") String owner,
                                               @PathVariable("repository-name") String repositoryName,
                                               @RequestHeader(value = "X-Auth-Token") String token) {
        return Mono.just(new RepoRequestParams(owner, repositoryName, token))
                .flatMap(gitHubClient::sendRequest)
                .flatMap(responseEntity -> Mono.justOrEmpty(responseEntity.getBody()));
    }
}