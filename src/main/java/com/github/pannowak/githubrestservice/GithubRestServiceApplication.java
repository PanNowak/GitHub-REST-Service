package com.github.pannowak.githubrestservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootApplication
public class GithubRestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubRestServiceApplication.class, args);
	}

	@Bean
	public WebClient getWebClient(@Value("${base.url}") String baseUrl) {
		return WebClient.builder().filter(this::logAndSendRequest).baseUrl(baseUrl).build();
	}

	private Mono<ClientResponse> logAndSendRequest(ClientRequest request, ExchangeFunction next) {
		log.info("Sending request {} : {}", request.method(), request.url());
		return next.exchange(request).doOnNext(clientResponse ->
				log.info("Received response : {}", clientResponse.statusCode()));
	}
}