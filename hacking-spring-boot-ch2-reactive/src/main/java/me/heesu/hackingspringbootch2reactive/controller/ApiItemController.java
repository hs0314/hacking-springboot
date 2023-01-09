package me.heesu.hackingspringbootch2reactive.controller;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.net.URI;

@RestController
public class ApiItemController {

    private final ItemRepository repository;

    public ApiItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/items")
    Flux<Item> findAll(){
       return this.repository.findAll();
    }

    @GetMapping("/api/items/{id}")
    Mono<Item> findOne(@PathVariable String id){
        return this.repository.findById(id);
    }

    @PostMapping("/api/items")
    public Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item){

        return item.flatMap(s -> this.repository.save(s))
                .map(savedItem -> ResponseEntity
                        .created(URI.create("/api/items"+savedItem.getId()))
                        .body(savedItem));
    }

    @PutMapping("/api/items/{id}")
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<Item> item,
                                              @PathVariable String id){
        return item.map(i -> new Item(id, i.getName(), i.getDescription(), i.getPrice()))
                .flatMap(this.repository::save) // flatMap을 이용해서 꺼내와야 data store에도 변경 적용됌
                .map(ResponseEntity::ok);
    }
}
