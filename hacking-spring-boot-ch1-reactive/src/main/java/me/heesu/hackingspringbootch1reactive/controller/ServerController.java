package me.heesu.hackingspringbootch1reactive.controller;

import me.heesu.hackingspringbootch1reactive.domain.Dish;
import me.heesu.hackingspringbootch1reactive.service.KitchenService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ServerController {

    private final KitchenService kitchen;

    public ServerController(KitchenService kitchen){
        this.kitchen = kitchen;
    }

    @GetMapping(value = "/server", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Dish> serveDishes(){
        return this.kitchen.getDishes();
    }

    @GetMapping(value = "/serverd-dishes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Dish> deliverDishes(){
        return this.kitchen.getDishes()
                .map(dish -> Dish.deliver(dish));
    }

}
