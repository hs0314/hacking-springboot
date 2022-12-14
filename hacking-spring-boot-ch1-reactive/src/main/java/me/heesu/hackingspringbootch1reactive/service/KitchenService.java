package me.heesu.hackingspringbootch1reactive.service;

import me.heesu.hackingspringbootch1reactive.domain.Dish;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class KitchenService {

    private List<Dish> menu = Arrays.asList(
            new Dish("Sesame chicken"),
            new Dish("Lo mein noddles"),
            new Dish("Beef")
    );

    private Random picker = new Random();

    /**
     * 요리 스트림 생성
     * @return
     */
    public Flux<Dish> getDishes() {
        return Flux.<Dish> generate(sink -> sink.next(randomDish()))
                .delayElements(Duration.ofMillis(250));
    }

    /**
     * 요리 무작위 선택
     * @return
     */
    private Dish randomDish() {
        return menu.get(picker.nextInt(menu.size()));
    }

}
