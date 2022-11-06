package me.heesu.hackingspringbootch2reactive.service;

import me.heesu.hackingspringbootch2reactive.domain.Cart;
import me.heesu.hackingspringbootch2reactive.domain.CartItem;
import me.heesu.hackingspringbootch2reactive.repository.CartRepository;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    CartService(ItemRepository itemRepository,
                CartRepository cartRepository){
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> addToCart(String cartId, String id){
        return this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart("My Cart"))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(id))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cartItem);
                        })
                        .orElseGet(() -> this.itemRepository.findById(id)
                            .map(CartItem::new)
                            .doOnNext(cartItem -> cart.getCartItems().add(cartItem)))
                        .map(cartItem -> cart)) // todo : Mono<CartItem> 을 어떻게 Cart 로 map할 수 있는가?
                .flatMap(cartRepository::save);
    }
}
