package me.heesu.hackingspringbootch2reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient // webTestClient 인스턴스 생성용
public class LoadingWebSiteIntegrationTest {

	@Autowired
	WebTestClient client;

	//xxx: 내장 컨테이너에 대한 웹컨트롤러/백엔드서비스 협력 테스트
	void test() {
		client.get().uri("/").exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.TEXT_HTML)
				.expectBody(String.class)
				.consumeWith(exchangeResult -> {
					assertThat(exchangeResult.getResponseBody()).contains("/add");
				});
	}

}
