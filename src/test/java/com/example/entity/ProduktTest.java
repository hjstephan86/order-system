package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class ProduktTest {

    @Test
    public void constructorAndGettersWork() {
        Produkt p = new Produkt("Widget", "nützlich", new BigDecimal("99.99"), 50);

        assertThat(p.getName()).isEqualTo("Widget");
        assertThat(p.getBeschreibung()).isEqualTo("nützlich");
        assertThat(p.getPreis()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(p.getLagerbestand()).isEqualTo(50);
    }

    @Test
    public void settersWork() {
        Produkt p = new Produkt();
        p.setId(7L);
        p.setName("Gadget");
        p.setBeschreibung("cool");
        p.setPreis(new BigDecimal("1.23"));
        p.setLagerbestand(5);

        assertThat(p.getId()).isEqualTo(7L);
        assertThat(p.getName()).isEqualTo("Gadget");
        assertThat(p.getBeschreibung()).isEqualTo("cool");
        assertThat(p.getPreis()).isEqualByComparingTo(new BigDecimal("1.23"));
        assertThat(p.getLagerbestand()).isEqualTo(5);
    }
}
