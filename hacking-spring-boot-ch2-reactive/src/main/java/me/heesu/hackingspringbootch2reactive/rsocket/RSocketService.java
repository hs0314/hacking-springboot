package me.heesu.hackingspringbootch2reactive.rsocket;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Mono;

// 특정 API에서 제공하는 Flux를 구독함으로써 스트림 트래픽을 받아갈 수 있다
@Controller
public class RSocketService {

    private final ItemRepository repository;

    private final Sinks.Many<Item> itemsSink;

    public RSocketService(ItemRepository repository) {
        this.repository = repository;

        this.itemsSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * 1. request - response
     *  - msg 유입을 리액티브하게 기다리다가 msg 유입 시, save() 처리
     *  - Mono<Item> 을 반환하고 이때 저장된 Item객체를 클라이언트에 적절한 backpressure를 통해서 내보낸다
     * @param item
     * @return
     */
    @MessageMapping("newItems.request-response")
    public Mono<Item> processNewItemViaRSocketRequestResponse(Item item){
        return this.repository.save(item)
                .doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem));
    }


    /**
     * 2. request - stream
     *  - Flux<Item>을 반환함으로써 R소켓 클라이언트 쪽 요구사항에 맞게 데이터를 리액티브하게 제공 가능
     * @return
     */
    @MessageMapping("newItems.request-stream")
    public Flux<Item> findItemsViaRSocketRequestStream(){
        return this.repository.findAll()
                .doOnNext(this.itemsSink::tryEmitNext);
    }

    /**
     * 3. 실행 후 망각
     *  - 결과에 대해서 클라이언트가 알 필요가 없으므로 Mono<Void>반환
     *  - 최종결과에 .then()을 통해서 데이터는 버리고 리액티브 스트림의 제어 신호만 반환
     * @param item
     * @return
     */
    @MessageMapping("newItems.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item){
        return this.repository.save(item)
                .doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem))
                .then();
    }

    /**
     * 신규 생성된 Item에 대한 모니터링용 Flux 반환
     * @return
     */
    @MessageMapping("newItems.monitor")
    public Flux<Item> monitorNewItems(){
        return this.itemsSink.asFlux();
    }

}
