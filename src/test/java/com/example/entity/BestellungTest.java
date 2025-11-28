package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BestellungTest {

    @Test
    public void constructorInitializesStatusAndDate() {
        Bestellung b = new Bestellung();

        assertThat(b.getStatus()).isEqualTo(BestellStatus.NEU);
        assertThat(b.getBestelldatum()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    public void addPositionSetsBackReferenceAndGetGesamtpreisSumsPositions() {
        Bestellung b = new Bestellung();

        Produkt p1 = new Produkt("A", null, new BigDecimal("2.50"), 10);
        Produkt p2 = new Produkt("B", null, new BigDecimal("1.25"), 10);

        BestellPosition pos1 = new BestellPosition(p1, 2); // 5.00
        BestellPosition pos2 = new BestellPosition(p2, 4); // 5.00

        b.addPosition(pos1);
        b.addPosition(pos2);

        assertThat(b.getPositionen()).containsExactly(pos1, pos2);
        assertThat(pos1.getBestellung()).isSameAs(b);
        assertThat(pos2.getBestellung()).isSameAs(b);

        assertThat(b.getGesamtpreis()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    public void settersAndGettersWork() {
        Bestellung b = new Bestellung();
        Kunde k = new Kunde("K", "k@k.de", "Addr");

        b.setId(123L);
        b.setKunde(k);
        LocalDateTime dt = LocalDateTime.of(2020, 1, 2, 3, 4);
        b.setBestelldatum(dt);
        b.setStatus(BestellStatus.VERSANDT);

        Produkt p = new Produkt("P", null, new BigDecimal("2.00"), 1);
        BestellPosition pos = new BestellPosition(p, 5); // 10.00
        List<BestellPosition> list = new ArrayList<>();
        list.add(pos);

        b.setPositionen(list);

        assertThat(b.getId()).isEqualTo(123L);
        assertThat(b.getKunde()).isSameAs(k);
        assertThat(b.getBestelldatum()).isEqualTo(dt);
        assertThat(b.getStatus()).isEqualTo(BestellStatus.VERSANDT);
        assertThat(b.getPositionen()).containsExactly(pos);
        // setPositionen does not set backrefs; getGesamtpreis should still sum position
        // prices
        assertThat(b.getGesamtpreis()).isEqualByComparingTo(new BigDecimal("10.00"));
    }
}
