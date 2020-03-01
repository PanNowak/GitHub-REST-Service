package com.github.pannowak.githubrestservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubRestServiceApplicationIT {

	private static final String TEST_TOKEN = "4776b500c4bb30ef7f676a7c59d99104ac44525b";

	@Autowired
	private WebTestClient testClient;

	@Test
	void givenCorrectRequestParamsShouldReturnRepositoryDetails() {
		testClient.get().uri("/repositories/allegro/SimplePricingService")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Auth-Token", TEST_TOKEN)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isNotEmpty()
				.jsonPath("$.fullName").isEqualTo("allegro/SimplePricingService")
				.jsonPath("$.description").isEqualTo("Simple implementation of pricing" +
				" service, which calculates price for creating offers.")
				.jsonPath("$.cloneUrl").isEqualTo("https://github.com/allegro/SimplePricingService.git")
				.jsonPath("$.stars").isEqualTo(3)
				.jsonPath("$.createdAt").isEqualTo("2015-03-09");
	}

	@Test
	void givenIncorrectTokenShouldReturnUnauthorizedStatusCode() {
		testClient.get().uri("/repositories/allegro/SimplePricingService")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Auth-Token", "wrongToken")
				.exchange()
				.expectStatus().isUnauthorized()
				.expectBody()
				.jsonPath("$.message")
				.isEqualTo("Wrong token! Please provide correct one.");
	}

	@Test
	void givenIncorrectPathShouldReturnNotFoundStatusCode() {
		testClient.get().uri("/repositories/madeUpName/notExisting")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Auth-Token", TEST_TOKEN)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.message")
				.isEqualTo("Owner and/or repository name could not be found!");
	}

	@Test
	void givenNoTokenShouldReturnBadRequestStatusCode() {
		testClient.get().uri("/repositories/allegro/SimplePricingService")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();
	}
}