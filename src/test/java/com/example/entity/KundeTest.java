package com.example.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.entity.Kunde.Geschlecht;

public class KundeTest {

    @Test
    public void constructorAndGettersWork() {
        Kunde k = new Kunde("Mustermann", "Max", "max@example.com");

        assertThat(k.getName()).isEqualTo("Mustermann");
        assertThat(k.getEmail()).isEqualTo("max@example.com");
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
        k.setVorname("Max");
        k.setName("Mustermann");
        k.setEmail("e@x.com");
        k.setBestellungen(list);

        assertThat(k.getId()).isEqualTo(11L);
        assertThat(k.getVorname()).isEqualTo("Max");
        assertThat(k.getName()).isEqualTo("Mustermann");
        assertThat(k.getEmail()).isEqualTo("e@x.com");
        assertThat(k.getBestellungen()).containsExactly(b);
        // setBestellungen does not set backrefs
        assertThat(b.getKunde()).isNull();
    }

    @Test
    public void vornameSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setVorname("Maria");

        assertThat(k.getVorname()).isEqualTo("Maria");
    }

    @Test
    public void vornameCanBeNull() {
        Kunde k = new Kunde();
        k.setVorname(null);

        assertThat(k.getVorname()).isNull();
    }

    @Test
    public void geburtstagSetterAndGetterWork() {
        Kunde k = new Kunde();
        LocalDate date = LocalDate.of(1990, 5, 15);
        k.setGeburtstag(date);

        assertThat(k.getGeburtstag()).isEqualTo(date);
    }

    @Test
    public void geburtstagCanBeNull() {
        Kunde k = new Kunde();
        k.setGeburtstag(null);

        assertThat(k.getGeburtstag()).isNull();
    }

    @Test
    public void landSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setLand("Deutschland");

        assertThat(k.getLand()).isEqualTo("Deutschland");
    }

    @Test
    public void landCanBeNull() {
        Kunde k = new Kunde();
        k.setLand(null);

        assertThat(k.getLand()).isNull();
    }

    @Test
    public void geschlechtSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setGeschlecht(Geschlecht.MAENNLICH);

        assertThat(k.getGeschlecht()).isEqualTo(Geschlecht.MAENNLICH);
    }

    @Test
    public void geschlechtCanBeWeiblich() {
        Kunde k = new Kunde();
        k.setGeschlecht(Geschlecht.WEIBLICH);

        assertThat(k.getGeschlecht()).isEqualTo(Geschlecht.WEIBLICH);
    }

    @Test
    public void geschlechtCanBeDivers() {
        Kunde k = new Kunde();
        k.setGeschlecht(Geschlecht.DIVERS);

        assertThat(k.getGeschlecht()).isEqualTo(Geschlecht.DIVERS);
    }

    @Test
    public void geschlechtCanBeNull() {
        Kunde k = new Kunde();
        k.setGeschlecht(null);

        assertThat(k.getGeschlecht()).isNull();
    }

    @Test
    public void telefonnummerSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setTelefonnummer("030-12345678");

        assertThat(k.getTelefonnummer()).isEqualTo("030-12345678");
    }

    @Test
    public void telefonnummerCanBeNull() {
        Kunde k = new Kunde();
        k.setTelefonnummer(null);

        assertThat(k.getTelefonnummer()).isNull();
    }

    @Test
    public void mobilnummerSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setMobilnummer("0170-9876543");

        assertThat(k.getMobilnummer()).isEqualTo("0170-9876543");
    }

    @Test
    public void mobilnummerCanBeNull() {
        Kunde k = new Kunde();
        k.setMobilnummer(null);

        assertThat(k.getMobilnummer()).isNull();
    }

    @Test
    public void strasseSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setStrasse("Hauptstraße");

        assertThat(k.getStrasse()).isEqualTo("Hauptstraße");
    }

    @Test
    public void strasseCanBeNull() {
        Kunde k = new Kunde();
        k.setStrasse(null);

        assertThat(k.getStrasse()).isNull();
    }

    @Test
    public void hausnummerSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setHausnummer("42a");

        assertThat(k.getHausnummer()).isEqualTo("42a");
    }

    @Test
    public void hausnummerCanBeNull() {
        Kunde k = new Kunde();
        k.setHausnummer(null);

        assertThat(k.getHausnummer()).isNull();
    }

    @Test
    public void postleitzahlSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setPostleitzahl("44787");

        assertThat(k.getPostleitzahl()).isEqualTo("44787");
    }

    @Test
    public void postleitzahlCanBeNull() {
        Kunde k = new Kunde();
        k.setPostleitzahl(null);

        assertThat(k.getPostleitzahl()).isNull();
    }

    @Test
    public void ortSetterAndGetterWork() {
        Kunde k = new Kunde();
        k.setOrt("Bochum");

        assertThat(k.getOrt()).isEqualTo("Bochum");
    }

    @Test
    public void ortCanBeNull() {
        Kunde k = new Kunde();
        k.setOrt(null);

        assertThat(k.getOrt()).isNull();
    }

    @Test
    public void allNewFieldsCanBeSetTogether() {
        Kunde k = new Kunde();
        k.setLand("Österreich");
        k.setGeschlecht(Geschlecht.DIVERS);
        k.setTelefonnummer("+43 1 234567");
        k.setMobilnummer("+43 664 1234567");

        assertThat(k.getLand()).isEqualTo("Österreich");
        assertThat(k.getGeschlecht()).isEqualTo(Geschlecht.DIVERS);
        assertThat(k.getTelefonnummer()).isEqualTo("+43 1 234567");
        assertThat(k.getMobilnummer()).isEqualTo("+43 664 1234567");
    }

    @Test
    public void allAddressFieldsCanBeSetTogether() {
        Kunde k = new Kunde();
        k.setVorname("Anna");
        k.setName("Schmidt");
        k.setStrasse("Universitätsstraße");
        k.setHausnummer("150");
        k.setPostleitzahl("44801");
        k.setOrt("Bochum");
        k.setLand("Deutschland");

        assertThat(k.getVorname()).isEqualTo("Anna");
        assertThat(k.getName()).isEqualTo("Schmidt");
        assertThat(k.getStrasse()).isEqualTo("Universitätsstraße");
        assertThat(k.getHausnummer()).isEqualTo("150");
        assertThat(k.getPostleitzahl()).isEqualTo("44801");
        assertThat(k.getOrt()).isEqualTo("Bochum");
        assertThat(k.getLand()).isEqualTo("Deutschland");
    }

    @Test
    public void geschlechtEnumHasThreeValues() {
        Geschlecht[] values = Geschlecht.values();

        assertThat(values).hasSize(3);
        assertThat(values).containsExactlyInAnyOrder(
                Geschlecht.MAENNLICH,
                Geschlecht.WEIBLICH,
                Geschlecht.DIVERS);
    }

    @Test
    public void geschlechtEnumValueOfWorks() {
        assertThat(Geschlecht.valueOf("MAENNLICH")).isEqualTo(Geschlecht.MAENNLICH);
        assertThat(Geschlecht.valueOf("WEIBLICH")).isEqualTo(Geschlecht.WEIBLICH);
        assertThat(Geschlecht.valueOf("DIVERS")).isEqualTo(Geschlecht.DIVERS);
    }
}