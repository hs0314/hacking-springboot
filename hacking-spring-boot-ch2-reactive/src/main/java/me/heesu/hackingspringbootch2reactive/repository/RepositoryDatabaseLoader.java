package me.heesu.hackingspringbootch2reactive.repository;

import me.heesu.hackingspringbootch2reactive.domain.Item;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RepositoryDatabaseLoader {

    @Bean
    CommandLineRunner initialize(BlockingItemRepository repository){
        return args -> {
            repository.deleteAll();

            repository.save(new Item("Alf alarm clock", "",19.99));
            repository.save(new Item("Smurt Tv", "",14.99));
        };
    }
}
