package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class KundeTest {

    @Test
    public void constructorAndGettersWork() {
        Kunde k = new Kunde("Max Mustermann", "max@example.com", "Musterstraße 1");

        assertThat(k.getName()).isEqualTo("Max Mustermann");
        assertThat(k.getEmail()).isEqualTo("max@example.com");
        assertThat(k.getAdresse()).isEqualTo("Musterstraße 1");
    }

    @Test
    public void addBestellungSetsBackReference() {
        Kunde k = new Kunde();
        Bestellung b = new Bestellung();

        k.addBestellung(b);

        assertThat(k.getBestellungen()).contains(b);
        assertThat(b.getKunde()).isSameAs(k);
    }

    @Test
    public void settersAndGettersWork() {
        Kunde k = new Kunde();
        Bestellung b = new Bestellung();
        b.setId(55L);

        List<Bestellung> list = new ArrayList<>();
        list.add(b);

        k.setId(11L);
        k.setName("Name");
        k.setEmail("e@x.com");
        k.setAdresse("addr");
        k.setBestellungen(list);

        assertThat(k.getId()).isEqualTo(11L);
        assertThat(k.getName()).isEqualTo("Name");
        assertThat(k.getEmail()).isEqualTo("e@x.com");
        assertThat(k.getAdresse()).isEqualTo("addr");
        assertThat(k.getBestellungen()).containsExactly(b);
        // setBestellungen does not set backrefs
        assertThat(b.getKunde()).isNull();
    }
}
