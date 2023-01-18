package me.heesu.hackingspringbootch2reactive.controller;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;

/**
 * Spring HATEOS 를 통해서 API에 행동유도성을 추가한다
 */
@RestController
public class HypermediaItemController {

    private final ItemRepository repository;

    public HypermediaItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hypermedia/api/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll(){

        HypermediaItemController controller = methodOn(HypermediaItemController.class);

        Mono<Link> aggregateRoot = linkTo(controller.findAll()).withSelfRel()
                .andAffordance(controller.addNewItem(null)) // Item 전체조회 시 addNewItem에 대한 링크를 가리킬 수 있음
                .toMono();


       return this.repository.findAll()
               .flatMap(item -> findOne((item.getId())))
               .collectList()
               .flatMap(models -> aggregateRoot.map(selfLink -> CollectionModel.of(models, selfLink)));
    }

    @GetMapping("/hypermedia/api/items/{id}")
    Mono<EntityModel<Item>> findOne(@PathVariable String id){
        // static method인 WebFluxLinkBuilder.methodOn()을 통해서 컨트롤러에 대한 프록시 생성
        HypermediaItemController controller = methodOn(HypermediaItemController.class);

        // findOne(id)에 대한 링크 생성
        Mono<Link> selfLink = linkTo(controller.findOne(id))
                .withSelfRel()
                .andAffordance(controller.updateItem(null, id)) // updateItem 경로를 현재 메서드와 연결
                .toMono();

        // 연결될 링크 생성
        Mono<Link> aggregateLink = linkTo(controller.findAll())
                .withRel(IanaLinkRelations.ITEM).toMono();


        // 해당 엔드포인트의 처리 로직결과와 생성한 링크를 EntityModel로 만들고 Mono로 감싸서 return
        // spring HATEOS를 통해 API 개발 시, 도메인객체와 링크를 조합해야한다 (ex. 제공되는 EntityModel 사용)
        // ** RepresentationModel, EntityModel, CollectionModel, PagedModel과 Link, Links 객체를 이용해서 하이퍼미디어 기능 제공
        return Mono.zip(this.repository.findById(id), selfLink, aggregateLink)
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
    }

    @PostMapping("/hypermedia/api/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item){
        return item.map(EntityModel::getContent)
                .flatMap(this.repository::save)
                .map(Item::getId)
                .flatMap(this::findOne)
                .map(newModel -> ResponseEntity.created(
                        // 링크 정보를 location 헤더에 저장하고 생성된 객체를 응답 본문에 담는다
                        newModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(newModel.getContent())
                );

    }

    @PutMapping("/hypermedia/api/items/{id}")
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item,
                                              @PathVariable String id){
        return item.map(EntityModel::getContent)
                .map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
                .flatMap(this.repository::save)
                .then(findOne(id))
                .map(model -> ResponseEntity.noContent().location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }

    /**
     * ALPS (Application-Level Profile Semantics)
     *  - spring HATEOS에서 제공, 하이퍼미디어 문서에 데이터에 대한 설명을 JSON 형식으로 제공하는 profile link 생성 가능
     *  - profile link는 자바스크립트 라이브러리에서 자동으로 form 형태로 생성 가능
     * @return
     */
    @GetMapping(value="/hypermedia/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
    public Alps profile(){
        return Alps.alps()
                .descriptor(Collections.singletonList(
                        descriptor().id(Item.class.getSimpleName()+"-repr")
                                .descriptor(
                                    Arrays.stream(Item.class.getDeclaredFields())
                                        .map(field -> descriptor().name(field.getName()).type(Type.SEMANTIC).build())
                                        .collect(Collectors.toList()))
                                .build()
                        )
                )
                .build();
    }
}
