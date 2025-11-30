package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RechnungsStatusTest {

    @Test
    public void enumContainsExpectedValues() {
        RechnungsStatus[] values = RechnungsStatus.values();

        assertThat(values).contains(RechnungsStatus.OFFEN, RechnungsStatus.BEZAHLT, RechnungsStatus.STORNIERT);
        assertThat(RechnungsStatus.valueOf("OFFEN")).isEqualTo(RechnungsStatus.OFFEN);
    }
}
