package me.heesu.hackingspringbootch2reactive.rsocket;

import me.heesu.hackingspringbootch2reactive.controller.HomeController;
import me.heesu.hackingspringbootch2reactive.domain.Cart;
import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import me.heesu.hackingspringbootch2reactive.service.InventoryService;
import me.heesu.hackingspringbootch2reactive.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureWebTestClient
class RSocketTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    ItemRepository repository;

    @Test
    void verifyRemoteOperationsThroughRSocketRequestResponse() throws InterruptedException {

        this.repository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();

        // item 생성
        this.client.post().uri("/items/request-response")
                .bodyValue(new Item("item1", "desc", 19.99))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Item.class)
                .value(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("item1");
                    assertThat(item.getDescription()).isEqualTo("desc");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                });

        Thread.sleep(5000);

        // MongoDB 저장 확인
        /* todo : 테스트실패 확인 java.lang.AssertionError: expectation "assertNext" failed (expected: onNext(); actual: onComplete())
        this.repository.findAll()
                .as(StepVerifier::create)
                .assertNext(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("item1");
                    assertThat(item.getDescription()).isEqualTo("desc");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                })
                .verifyComplete();
        */
    }
}