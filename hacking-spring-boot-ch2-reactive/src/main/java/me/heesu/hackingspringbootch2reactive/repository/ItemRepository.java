package me.heesu.hackingspringbootch2reactive.repository;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveCrudRepository<Item, String>,
                                        ReactiveQueryByExampleExecutor<Item> {

    Flux<Item> findByNameContaining(String partialName); // 검색에 대한 기본 기능 제공

    //xxx: 특정 형식에 맞게 메서드명을 작성 시, 기본적인 crud에 대한 처리는 굳이 쿼리작성이 필요없이 spring-data에서 제공해준다
    // * 복잡한 검색쿼리가 필요한 경우는 ExampleQuery로 해결 가능}
}
