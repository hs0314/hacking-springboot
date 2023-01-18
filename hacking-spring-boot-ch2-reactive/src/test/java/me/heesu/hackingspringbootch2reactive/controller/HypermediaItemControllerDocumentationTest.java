package me.heesu.hackingspringbootch2reactive.controller;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import me.heesu.hackingspringbootch2reactive.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;


@WebFluxTest(HypermediaItemController.class)
@AutoConfigureRestDocs // spring rest docs에서 필요한 내용 자동 설정
class HypermediaItemControllerDocumentationTest {

    @Autowired
    private WebTestClient client;

    @MockBean // mock객체 생성 + 자동주입
    InventoryService service;

    @MockBean
    ItemRepository repository;

    @Test
    void findOneItem(){

        when(repository.findById("item1")).thenReturn(
                Mono.just(new Item("item1", "item1", "item1", 19.99))
        );

        this.client.get().uri("/hypermedia/api/items/item1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()),
                        links(
                                linkWithRel("self").description("이 `Item`에 대한 공식 링크"),
                                linkWithRel("item").description("`Item` 목록 링크")
                        ))
                ); // document( adoc파일 생성 디렉터리명 , response에 대한 처리)


    }

    @Test
    void findOneItemAffordances(){
        // spring rest docs를 사용해서 API 행동 유도성 링크추가 코드 테스트
        when(repository.findById("item1")).thenReturn(
                Mono.just(new Item("item1", "item1", "item1", 19.99))
        );

        this.client.get().uri("/hypermedia/api/items/item1")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("single-item-affordances", preprocessResponse(prettyPrint())));
                // document( adoc파일 생성 디렉터리명 , response에 대한 처리)



    }
}