package com.github.pannowak.githubrestservice.control;

import com.github.pannowak.githubrestservice.entity.RepoRequestParams;
import com.github.pannowak.githubrestservice.entity.RepositoryDetails;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class GitHubClientTest {

    private static MockWebServer mockWebServer;

    private GitHubClient gitHubClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        gitHubClient = new GitHubClient(WebClient.create(baseUrl));
    }

    @Test
    void shouldReturnRepositoryDetailsIfResponseBodyIsSuccessfullyReturned()
            throws InterruptedException, IOException {
        prepareMockResponse(HttpStatus.OK.value(), getExampleResponseBody());

        RepositoryDetails repositoryDetails = sendRequestAndGetResponseBody();

        assertCorrectRequestWasSent();
        assertCorrectResponseBody(repositoryDetails);
    }

    @Test
    void shouldThrowResponseStatusExceptionIfUnauthorizedStatusCodeIsReturned() {
        prepareMockResponse(HttpStatus.UNAUTHORIZED.value(), "");
        assertThrows(ResponseStatusException.class, this::sendRequestAndGetResponseBody);
    }

    @Test
    void shouldThrowResponseStatusExceptionIfNotFoundStatusCodeIsReturned() {
        prepareMockResponse(HttpStatus.NOT_FOUND.value(), "");
        assertThrows(ResponseStatusException.class, this::sendRequestAndGetResponseBody);
    }

    @Test
    void shouldThrowWebClientResponseExceptionIfOtherErrorStatusCodeIsReturned() {
        prepareMockResponse(HttpStatus.I_AM_A_TEAPOT.value(), "");
        assertThrows(WebClientException.class, this::sendRequestAndGetResponseBody);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private void prepareMockResponse(int responseCode, String responseBody) {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(responseCode)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json; charset=utf-8"));
    }

    private String getExampleResponseBody() throws IOException {
        return StreamUtils.copyToString(getClass().getClassLoader()
                .getResourceAsStream("example_response.json"), Charset.defaultCharset());
    }

    private RepositoryDetails sendRequestAndGetResponseBody() {
        RepoRequestParams params = new RepoRequestParams(
                "testOwner", "testRepo", "testToken");
        return gitHubClient.sendRequest(params)
                .flatMap(responseEntity -> Mono.justOrEmpty(responseEntity.getBody()))
                .block();
    }

    private void assertCorrectRequestWasSent() throws InterruptedException {
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertAll(
                () -> assertEquals("GET", recordedRequest.getMethod()),
                () -> assertEquals("/testOwner/testRepo", recordedRequest.getPath()),
                () -> assertEquals("Bearer testToken",
                        recordedRequest.getHeader("Authorization"))
        );
    }

    private void assertCorrectResponseBody(RepositoryDetails repositoryDetails) {
        assertNotNull(repositoryDetails);
        assertAll(
                () -> assertEquals("allegro/SimplePricingService",
                        repositoryDetails.getFullName()),
                () -> assertEquals("Simple implementation of pricing service.",
                        repositoryDetails.getDescription()),
                () -> assertEquals("https://github.com/allegro/SimplePricingService.git",
                        repositoryDetails.getCloneUrl()),
                () -> assertEquals(3, repositoryDetails.getStars()),
                () -> assertEquals("2015-03-09", repositoryDetails.getCreatedAt())
        );
    }
}