package me.heesu.hackingspringbootch2reactive;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ContextConfiguration
public class RabbitMqTest {

    @Container
    static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

    @Autowired
    WebTestClient client;

    @Autowired
    ItemRepository repository;

    // 환경설정 내용을 동적으로 추가
    //@DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry){
        registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
    }

    //@Test
    void verifyMessagingThroughAmqp() throws InterruptedException{
        //직전 데이터 전체 삭제
        repository.deleteAll();

        // create item1
        this.client.post().uri("/items")
                .bodyValue(new Item("item1", "desc", 5.00))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        Thread.sleep(1500L); // item생성 msg를 순차적으로 처리하기 위해서 sleep() 사용

        // create item2
        this.client.post().uri("/items")
                .bodyValue(new Item("item2", "desc", 9.99))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        Thread.sleep(2000L);

        this.repository.findAll()
                .as(StepVerifier::create)
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("item1");
                    assertThat(item.getDescription()).isEqualTo("desc");
                    assertThat(item.getPrice()).isEqualTo(5.00);
                    return true;
                })
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("item2");
                    assertThat(item.getDescription()).isEqualTo("desc");
                    assertThat(item.getPrice()).isEqualTo(9.99);
                    return true;
                })
                .verifyComplete();
    }
}
