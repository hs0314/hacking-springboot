package me.heesu.hackingspringbootch2reactive;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest // spring-data-mongodb 관련 모든 기능 사용 가능하고 다른 bean에 대한 정의는 무시 => 테스트 간소화
class MongoDbSliceTest {

	@Autowired
	ItemRepository itemRepository;

	@Test
	void itemRepositorySavesItems(){
		Item item = new Item("name", "desc", 1.99);

		itemRepository.save(item)
				.as(StepVerifier::create)
				.expectNextMatches(i -> {
					assertThat(i.getId()).isNotNull();
					assertThat(i.getName()).isEqualTo("name");
					assertThat(i.getDescription()).isEqualTo("desc");
					assertThat(i.getPrice()).isEqualTo(1.99);

					return true;
				})
				.verifyComplete();
	}

}
