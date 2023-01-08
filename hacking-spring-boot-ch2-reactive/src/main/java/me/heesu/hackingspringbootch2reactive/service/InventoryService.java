package me.heesu.hackingspringbootch2reactive.service;

import me.heesu.hackingspringbootch2reactive.domain.Cart;
import me.heesu.hackingspringbootch2reactive.domain.CartItem;
import me.heesu.hackingspringbootch2reactive.domain.Item;
import me.heesu.hackingspringbootch2reactive.repository.CartRepository;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InventoryService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    InventoryService(ItemRepository itemRepository,
                     CartRepository cartRepository){
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> getCart(String cartId){
        return this.cartRepository.findById(cartId).defaultIfEmpty(new Cart("My Cart"));
    }

    public Flux<Item> getInventory(){
        return this.itemRepository.findAll().doOnNext(System.out::println);
    }

    public Mono<Cart> addToCart(String cartId, String id){
        /*
         리액터 플로우에서의 로깅 예시
          - 내부적으로 수행되는 작업과 리액터 흐름까지 같이 로그로 찍힘
          - 구독은 리액터 플로우에서 가장 마지막에 발생하지만 로그 기준으로는 onSubscribe가 가장 먼저 찍히고 최종적으로 onComplete()
         */

        return this.cartRepository.findById(cartId)
                .log("##### foundCart")
                .defaultIfEmpty(new Cart("My Cart"))
                .log("##### emptyCart")
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(id))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart).log("##### newCartItem");
                        })
                        .orElseGet(() -> {
                            return this.itemRepository.findById(id)
                                .log("##### fetchedItem")
                                .map(CartItem::new)
                                .log("##### cartItem")
                                .map(cartItem -> {
                                    cart.getCartItems().add(cartItem);
                                    return cart;
                                }).log("##### addedCartItem");
                        }))
                .log("##### cartWithAnotherItem")
                .flatMap(cartRepository::save)
                .log("##### savedCart");
    }
}
