package me.heesu.hackingspringbootch2reactive.service;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SearchService {

    private final ItemRepository itemRepository;

    private final ReactiveFluentMongoOperations fluentOperations; // FluentMongoOperations의 리액티브 버전

    public SearchService(ItemRepository itemRepository,
                         ReactiveFluentMongoOperations fluentOperations){
        this.itemRepository = itemRepository;
        this.fluentOperations = fluentOperations;
    }

    /**
     * ExampleQuery를 활용한 복잡한 조건에 대한 데이터 조회
     * @param name
     * @param description
     * @param useAnd
     * @return
     */
    public Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0); // price는  not null이므로 0.0으로 셋팅

        ExampleMatcher matcher = (useAnd ?
                ExampleMatcher.matchingAll() : ExampleMatcher.matchingAny())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price");

        Example<Item> probe = Example.of(item, matcher);

        return itemRepository.findAll(probe);
    }

    /**
     * 평문형 검색 연산
     *  - 비어있는 필드나 부분 일치 기능에 대해서는 평문형API 사용불가
     * @param name
     * @param description
     * @return
     */
    public Flux<Item> searchByFluentExample(String name, String description){
        return fluentOperations.query(Item.class)
                .matching(query(where("TV tray").is(name).and("Smurf").is(description)))
                .all();
    }

    /**
     * 평문형 API와 Example을 같이 사용한 쿼리 검색
     * }
     * @param name
     * @param description
     * @param useAnd
     * @return
     */
    public Flux<Item> searchByFluentExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0); // price는  not null이므로 0.0으로 셋팅

        ExampleMatcher matcher = (useAnd ?
                ExampleMatcher.matchingAll() : ExampleMatcher.matchingAny())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price");

        return fluentOperations.query(Item.class)
                .matching(query(byExample(Example.of(item, matcher))))
                .all();
    }
}
