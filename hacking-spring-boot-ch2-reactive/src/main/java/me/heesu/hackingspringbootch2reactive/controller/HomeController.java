package me.heesu.hackingspringbootch2reactive.controller;

import me.heesu.hackingspringbootch2reactive.domain.Cart;
import me.heesu.hackingspringbootch2reactive.repository.CartRepository;
import me.heesu.hackingspringbootch2reactive.repository.ItemRepository;
import me.heesu.hackingspringbootch2reactive.service.InventoryService;
import me.heesu.hackingspringbootch2reactive.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

    private InventoryService inventoryService;
    private SearchService searchService;

    public HomeController(InventoryService inventoryService,
                          SearchService searchService){
        this.inventoryService = inventoryService;
        this.searchService = searchService;
    }

    @GetMapping
    Mono<Rendering> home(){
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", inventoryService.getInventory())
                .modelAttribute("cart", inventoryService.getCart("My Cart"))
                .build());
    }

    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id){

        return this.inventoryService.addToCart("My Cart", id)
                .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    Mono<Rendering> search(@RequestParam(required = false) String name,
                           @RequestParam(required = false) String description,
                           @RequestParam boolean useAnd){
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", searchService.searchByExample(name, description, useAnd))
                .modelAttribute("cart", inventoryService.getCart("My Cart"))
                .build());

    }
}
