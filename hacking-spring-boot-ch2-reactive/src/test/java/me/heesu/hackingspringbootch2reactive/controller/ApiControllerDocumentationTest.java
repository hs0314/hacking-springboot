package me.heesu.hackingspringbootch2reactive.controller;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import me.heesu.hackingspringbootch2reactive.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;


@WebFluxTest(ApiItemController.class)
@AutoConfigureRestDocs // spring rest docs에서 필요한 내용 자동 설정
class ApiControllerDocumentationTest {

    @Autowired
    private WebTestClient client;

    @MockBean // mock객체 생성 + 자동주입
    InventoryService service;

    @MockBean
    ItemRepository repository;

    @Test
    void findingAllItems(){

        when(repository.findAll()).thenReturn(
                Flux.just(new Item("item1", "item1", "item1", 19.99))
        );

        this.client.get().uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findAll", preprocessResponse(prettyPrint()))); // 이 부분에서 문서 생성
                // document( adoc파일 생성 디렉터리명 , response에 대한 처리)
    }

    @Test
    void poseNewItem(){

        when(repository.save(any())).thenReturn(
                Mono.just(new Item("item1", "item1", "item1", 19.99))
        );

        this.client.post().uri("/api/items")
                .bodyValue(new Item("item1", "item1", 19.99))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("post-new-item", preprocessResponse(prettyPrint()))); // 이 부분에서 문서 생성
        // document( adoc파일 생성 디렉터리명 , response에 대한 처리)
    }
}