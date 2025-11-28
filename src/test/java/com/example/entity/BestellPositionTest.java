package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class BestellPositionTest {

    @Test
    public void constructorSetsEinzelpreisFromProdukt() {
        Produkt p = new Produkt("Test", "desc", new BigDecimal("12.34"), 10);
        BestellPosition bp = new BestellPosition(p, 2);

        assertThat(bp.getEinzelpreis()).isEqualByComparingTo(new BigDecimal("12.34"));
        assertThat(bp.getMenge()).isEqualTo(2);
        assertThat(bp.getProdukt()).isSameAs(p);
    }

    @Test
    public void getGesamtpreisReturnsEinzelpreisTimesMenge() {
        Produkt p = new Produkt("Produkt A", "Beschreibung", new BigDecimal("5.50"), 100);
        BestellPosition bp = new BestellPosition(p, 3);

        BigDecimal expected = new BigDecimal("5.50").multiply(new BigDecimal(3));
        assertThat(bp.getGesamtpreis()).isEqualByComparingTo(expected);
    }

    @Test
    public void settersAndGettersWork() {
        BestellPosition bp = new BestellPosition();
        Produkt p = new Produkt("X", null, new BigDecimal("1.00"), 1);

        bp.setId(42L);
        bp.setProdukt(p);
        bp.setMenge(7);
        bp.setEinzelpreis(new BigDecimal("1.00"));

        assertThat(bp.getId()).isEqualTo(42L);
        assertThat(bp.getProdukt()).isSameAs(p);
        assertThat(bp.getMenge()).isEqualTo(7);
        assertThat(bp.getEinzelpreis()).isEqualByComparingTo(new BigDecimal("1.00"));

        // Gesamtpreis should reflect the set einzelpreis and menge
        assertThat(bp.getGesamtpreis()).isEqualByComparingTo(new BigDecimal("7.00"));
    }

    @Test
    public void bestellungSetterAndGetter() {
        BestellPosition bp = new BestellPosition();
        Bestellung b = new Bestellung();
        b.setId(99L);

        bp.setBestellung(b);

        assertThat(bp.getBestellung()).isSameAs(b);
    }
}
