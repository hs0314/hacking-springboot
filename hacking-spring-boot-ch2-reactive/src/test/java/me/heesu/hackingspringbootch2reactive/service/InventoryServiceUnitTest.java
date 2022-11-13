package me.heesu.hackingspringbootch2reactive.service;

import me.heesu.hackingspringbootch2reactive.domain.Cart;
import me.heesu.hackingspringbootch2reactive.domain.CartItem;
import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.CartRepository;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class) // test handler를 지정할 수 있는 JUnit5 API
class InventoryServiceUnitTest {

    InventoryService inventoryService;

    @MockBean(name="itemRepository")
    private ItemRepository itemRepository;

    @MockBean(name="cartRepository")
    private CartRepository cartRepository;

    @BeforeEach
    void setUp(){
        // itemRepository = mock(ItemRepository.class) // 해당코드를 @MockBean 어노테이션으로 대체

        Item item = new Item("item1", "Tv tray", "Alf Tv Tray", 19.99);
        CartItem cartItem = new CartItem(item);
        Cart cart = new Cart("My Cart", Collections.singletonList(cartItem));

        // stub 상호작용 셋팅
        // 리액터 테스트를 위해서 Mono 반환
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(cart));

        inventoryService = new InventoryService(itemRepository, cartRepository);
    }

    //xxx: 리액티브 테스트 코드 예시
    // StepVerifier -> 리액터 테스트 모듈
    // 코드의 기능 뿐 아니라 리액티브 스트림 시그널(ex. onSubscribe, onComplete, onNext, onError)도 테스트
    @Test
    void addItemToEmptyCartShouldProduceOneCartItem(){
        inventoryService.addToCart("My Cart", "item1")
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(new Item("item1", "Tv tray", "Alf Tv Tray", 19.99));

                    return true;
                })
                .verifyComplete();
    }
}