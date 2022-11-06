package me.heesu.hackingspringbootch2reactive.repository;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface BlockingItemRepository extends CrudRepository<Item, String> {

}
