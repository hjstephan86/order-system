package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class RechnungTest {

    @Test
    public void defaultConstructorInitializesErstellungsdatumAndDefaultStatus() {
        Rechnung r = new Rechnung();

        assertThat(r.getErstellungsdatum()).isNotNull();
        assertThat(r.getStatus()).isEqualTo(RechnungsStatus.OFFEN);
        assertThat(r.getId()).isNull();
    }

    @Test
    public void settersAndGettersWork() {
        Rechnung r = new Rechnung();
        r.setId(42L);
        r.setRechnungsnummer("INV-2025-0001");

        Bestellung b = new Bestellung();
        r.setBestellung(b);

        LocalDateTime now = LocalDateTime.of(2025, 11, 30, 12, 0);
        r.setErstellungsdatum(now);

        r.setGesamtbetrag(new BigDecimal("123.45"));
        r.setStatus(RechnungsStatus.BEZAHLT);

        LocalDateTime bezahltAm = LocalDateTime.of(2025, 12, 1, 9, 30);
        r.setBezahltAm(bezahltAm);
        r.setBezahltVon("system_user");

        assertThat(r.getId()).isEqualTo(42L);
        assertThat(r.getRechnungsnummer()).isEqualTo("INV-2025-0001");
        assertThat(r.getBestellung()).isSameAs(b);
        assertThat(r.getErstellungsdatum()).isEqualTo(now);
        assertThat(r.getGesamtbetrag()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(r.getStatus()).isEqualTo(RechnungsStatus.BEZAHLT);
        assertThat(r.getBezahltAm()).isEqualTo(bezahltAm);
        assertThat(r.getBezahltVon()).isEqualTo("system_user");
    }
}
