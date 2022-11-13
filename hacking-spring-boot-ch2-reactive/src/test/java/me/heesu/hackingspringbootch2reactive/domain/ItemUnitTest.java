package me.heesu.hackingspringbootch2reactive.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//xxx : 도메인 객체는 다른 계층에 대한 의존관계가 없기 때문에 테스트코드를 작성하기 쉬움
class ItemUnitTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void itemBasicShouldWork(){
        Item sample = new Item("item1", "Tv tray", "Alf Tv Tray", 19.99);
        Item sample2 = new Item("item1", "Tv tray", "Alf Tv Tray", 19.99);

        assertThat(sample.getId()).isEqualTo("item1");
        assertThat(sample.getName()).isEqualTo("Tv tray");
        assertThat(sample.getDescription()).isEqualTo("Alf Tv Tray");
        assertThat(sample.getPrice()).isEqualTo(19.99);

        assertThat(sample).isEqualTo(sample2);

    }
}