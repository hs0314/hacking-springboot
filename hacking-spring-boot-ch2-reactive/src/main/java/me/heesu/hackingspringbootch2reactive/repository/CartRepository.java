package me.heesu.hackingspringbootch2reactive.repository;

import me.heesu.hackingspringbootch2reactive.domain.Cart;
import me.heesu.hackingspringbootch2reactive.domain.Item;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, String> {

}
