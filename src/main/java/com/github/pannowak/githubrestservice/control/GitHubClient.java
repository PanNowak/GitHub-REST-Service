package com.github.pannowak.githubrestservice.control;

import com.github.pannowak.githubrestservice.entity.RepoRequestParams;
import com.github.pannowak.githubrestservice.entity.RepositoryDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GitHubClient {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String WRONG_TOKEN_MESSAGE = "Wrong token! Please provide correct one.";
    private static final String NOT_FOUND_MESSAGE = "Owner and/or repository name could not be found!";

    private final WebClient webClient;

    GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ResponseEntity<RepositoryDetails>> sendRequest(RepoRequestParams params) {
        return webClient.get()
                .uri("/{owner}/{repository-name}", params.getOwner(), params.getRepositoryName())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER_NAME, params.getToken())
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, clientResponse ->
                        getResponseStatusException(clientResponse, HttpStatus.UNAUTHORIZED, WRONG_TOKEN_MESSAGE))
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        getResponseStatusException(clientResponse, HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE))
                .toEntity(RepositoryDetails.class);
    }

    private Mono<ResponseStatusException> getResponseStatusException(ClientResponse clientResponse,
                                                                     HttpStatus status, String message) {
        return clientResponse.releaseBody()
                .then(Mono.just(new ResponseStatusException(status, message)));
    }
}