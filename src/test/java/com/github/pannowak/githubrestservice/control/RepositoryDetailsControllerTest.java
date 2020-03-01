package com.github.pannowak.githubrestservice.control;

import com.github.pannowak.githubrestservice.entity.RepoRequestParams;
import com.github.pannowak.githubrestservice.entity.RepositoryDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryDetailsControllerTest {

    private static final RepoRequestParams CORRECT_REQUEST_PARAMS = new RepoRequestParams(
            "trueOwner", "testName", "validToken");

    @Mock
    private GitHubClient gitHubClient;

    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        when(gitHubClient.sendRequest(CORRECT_REQUEST_PARAMS))
                .thenReturn(Mono.just(ResponseEntity.ok(getTestRepoDetails())));
        testClient = WebTestClient.bindToController(
                new RepositoryDetailsController(gitHubClient)).build();
    }

    @Test
    void givenCorrectRequestParamsShouldReturnRepositoryDetails() {
        testClient.get()
                .uri("/repositories/trueOwner/testName")
                .header("X-Auth-Token", "validToken")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.fullName").isEqualTo("testFullName")
                .jsonPath("$.description").isEqualTo("testDescription")
                .jsonPath("$.cloneUrl").isEqualTo("testCloneUrl")
                .jsonPath("$.stars").isEqualTo(4)
                .jsonPath("$.createdAt").isEqualTo("2020-01-01");
    }

    private RepositoryDetails getTestRepoDetails() {
        RepositoryDetails repoDetails = new RepositoryDetails();
        repoDetails.setFullName("testFullName");
        repoDetails.setDescription("testDescription");
        repoDetails.setCloneUrl("testCloneUrl");
        repoDetails.setStars(4);
        repoDetails.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
        return repoDetails;
    }
}