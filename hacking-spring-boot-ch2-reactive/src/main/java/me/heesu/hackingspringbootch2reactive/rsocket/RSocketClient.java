package me.heesu.hackingspringbootch2reactive.rsocket;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.netty.transport.ClientTransport;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;

@RestController
public class RSocketClient {

    // RSocketRequester를 사용해서 spring과의 연동
    private final Mono<RSocketRequester> requester;

    public RSocketClient(RSocketRequester requester) {
        this.requester = Mono.just(requester);
        //this.requester = config.getRSocketRequester();

    }

    @PostMapping("/items/request-response")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item){

        return this.requester
                .flatMap(requester -> requester
                        .route("newItems.request-response")
                        .data(item)
                        .retrieveMono(Item.class)
                )
                .map(savedItem -> ResponseEntity.created(URI.create("/items/request-response")).body(savedItem));
    }
}
