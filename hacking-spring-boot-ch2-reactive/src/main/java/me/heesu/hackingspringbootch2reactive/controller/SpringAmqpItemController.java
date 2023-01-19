package me.heesu.hackingspringbootch2reactive.controller;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

@RestController
public class SpringAmqpItemController {

    private static final Logger log = LoggerFactory.getLogger(SpringAmqpItemController.class);

    private final AmqpTemplate template;

    public SpringAmqpItemController(AmqpTemplate template) {
        this.template = template;
    }

    @PostMapping("/items")
    Mono<ResponseEntity<?>> addNewItemUsingSpringAmqp(@RequestBody Mono<Item> item){

        // blocking이 발생할 수 있는 부분은 리액터 플로우에서 스케쥴러 변경을 통해서 메이 리액터 플로우에 블로킹 영향을 전파하지 않도록함

        return item.subscribeOn(Schedulers.boundedElastic()) // 스케쥴러를 통해서 blocking발생 작업은 별도의 스레드에서 실행
                .flatMap(content -> {
                    return Mono.fromCallable(() -> {

                        // blocking 발생
                        this.template.convertAndSend(
                                "hacking-spring-boot",
                                "new-items-spring-amqp",
                                content
                        );

                        // 요청에 대한 201 created 반환
                        return ResponseEntity.created(URI.create("/items")).build();
                    });
                });
    }
}
