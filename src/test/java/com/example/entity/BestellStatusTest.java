package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BestellStatusTest {

    @Test
    public void enumContainsExpectedValues() {
        BestellStatus[] values = BestellStatus.values();

        assertThat(values).contains(BestellStatus.NEU,
                BestellStatus.IN_BEARBEITUNG,
                BestellStatus.VERSENDET,
                BestellStatus.STORNIERT);
    }

    @Test
    public void valueOfReturnsCorrectEnum() {
        assertThat(BestellStatus.valueOf("NEU")).isEqualTo(BestellStatus.NEU);
    }
}
