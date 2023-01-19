package me.heesu.hackingspringbootch2reactive.service;

import me.heesu.hackingspringbootch2reactive.controller.SpringAmqpItemController;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import me.heesu.hackingspringbootch2reactive.domain.Item;

@Service
public class SpringAmqpItemService {
    private static final Logger log = LoggerFactory.getLogger(SpringAmqpItemController.class);

    private final ItemRepository repository;

    public SpringAmqpItemService(ItemRepository repository) {
        this.repository = repository;
    }

    // 스프링 AMQP 메세지 리스너로 등록 -> 가장 효과적인 캐시, 풀링 매커니즘 적용
    // Item을 Serializable을 구현하도록 하면 직렬화할 수 있지만 최선의 방법은 아니므로 다른 대안이 POJO를 json string으로 변환해서 msg에 담도록 한다.
    @RabbitListener(
            ackMode = "MANUAL",
            bindings = @QueueBinding(
                    value = @Queue, // 지속성 없는 익명 큐 사용
                    exchange = @Exchange("hacking-spring-boot"),
                    key = "new-items-spring-amqp"
            ))
    public Mono<Void> processNewItemsViaSpringAmqp(Item item){
        log.debug("Consuming => " + item);
        return this.repository.save(item).then();
    }
}
