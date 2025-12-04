package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.entity.BestellStatus;
import com.example.entity.Bestellung;
import com.example.entity.Kunde;
import com.example.entity.Produkt;
import com.example.repository.BestellungRepository;
import com.example.repository.KundeRepository;
import com.example.repository.ProduktRepository;

import jakarta.inject.Inject;

@ExtendWith(ArquillianExtension.class)
public class BestellServiceIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.example.entity")
                .addPackages(true, "com.example.repository")
                .addPackages(true, "com.example.service")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private BestellService bestellService;

    @Inject
    private KundeRepository kundeRepository;

    @Inject
    private ProduktRepository produktRepository;

    @Inject
    private BestellungRepository bestellungRepository;

    private Kunde testKunde;
    private Produkt testProdukt1;
    private Produkt testProdukt2;

    private static long testCounter = 0;

    @BeforeEach
    public void setup() {
        // Verwende eindeutige E-Mail für jeden Test
        testCounter++;
        testKunde = kundeRepository.save(
                new Kunde("Test", "Kunde", "test" + testCounter + "@example.com"));

        testProdukt1 = produktRepository.save(
                new Produkt("Produkt 1", "Beschreibung 1", new BigDecimal("100"), 10));

        testProdukt2 = produktRepository.save(
                new Produkt("Produkt 2", "Beschreibung 2", new BigDecimal("50"), 20));
    }

    @Test
    public void testErstelleBestellung() {
        BestellPositionDTO pos1 = new BestellPositionDTO(testProdukt1.getId(), 2);
        BestellPositionDTO pos2 = new BestellPositionDTO(testProdukt2.getId(), 3);

        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos1, pos2));

        assertNotNull(bestellung.getId());
        assertEquals(2, bestellung.getPositionen().size());
        assertEquals(BestellStatus.NEU, bestellung.getStatus());

        Produkt p1 = produktRepository.findById(testProdukt1.getId()).get();
        assertEquals(8, p1.getLagerbestand());

        Produkt p2 = produktRepository.findById(testProdukt2.getId()).get();
        assertEquals(17, p2.getLagerbestand());
    }

    @Test
    public void testErstelleBestellungMitUnzureichendemLagerbestand() {
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 100);

        try {
            bestellService.erstelleBestellung(testKunde.getId(), Arrays.asList(pos));
            fail("Expected RuntimeException due to insufficient stock");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Nicht genug Lagerbestand"));
        }
    }

    @Test
    public void testAktualisiereBestellstatus() {
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));

        bestellService.aktualisiereStatus(bestellung.getId(), BestellStatus.IN_BEARBEITUNG);

        Bestellung updated = bestellungRepository.findById(bestellung.getId()).get();
        assertEquals(BestellStatus.IN_BEARBEITUNG, updated.getStatus());
    }

    @Test
    public void testBestellungGesamtpreis() {
        BestellPositionDTO pos1 = new BestellPositionDTO(testProdukt1.getId(), 2);
        BestellPositionDTO pos2 = new BestellPositionDTO(testProdukt2.getId(), 3);

        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos1, pos2));

        BigDecimal gesamtpreis = bestellung.getGesamtpreis();
        assertEquals(0, gesamtpreis.compareTo(new BigDecimal("350")));
    }
}
