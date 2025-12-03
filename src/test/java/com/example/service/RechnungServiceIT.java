package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
import com.example.entity.Rechnung;
import com.example.entity.RechnungsStatus;
import com.example.repository.BestellungRepository;
import com.example.repository.KundeRepository;
import com.example.repository.ProduktRepository;
import com.example.repository.RechnungRepository;

import jakarta.inject.Inject;

@ExtendWith(ArquillianExtension.class)
public class RechnungServiceIT {

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
    private RechnungService rechnungService;

    @Inject
    private BestellService bestellService;

    @Inject
    private KundeRepository kundeRepository;

    @Inject
    private ProduktRepository produktRepository;

    @Inject
    private BestellungRepository bestellungRepository;

    @Inject
    private RechnungRepository rechnungRepository;

    private Kunde testKunde;
    private Produkt testProdukt1;
    private Produkt testProdukt2;

    private static long testCounter = 0;

    @BeforeEach
    public void setup() {
        // Use unique email for each test
        testCounter++;
        testKunde = kundeRepository.save(
                new Kunde("Test Kunde", "test" + testCounter + "@example.com", "Test Adresse"));

        testProdukt1 = produktRepository.save(
                new Produkt("Produkt 1", "Beschreibung 1", new BigDecimal("100"), 10));

        testProdukt2 = produktRepository.save(
                new Produkt("Produkt 2", "Beschreibung 2", new BigDecimal("50"), 20));
    }

    @Test
    public void testErstelleRechnung() {
        // Create an order first
        BestellPositionDTO pos1 = new BestellPositionDTO(testProdukt1.getId(), 2);
        BestellPositionDTO pos2 = new BestellPositionDTO(testProdukt2.getId(), 3);

        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos1, pos2));

        // Create invoice for the order
        Rechnung rechnung = rechnungService.erstelleRechnung(bestellung.getId());

        assertNotNull(rechnung.getId());
        assertNotNull(rechnung.getRechnungsnummer());
        assertTrue(rechnung.getRechnungsnummer().startsWith("RE-"));
        assertEquals(RechnungsStatus.OFFEN, rechnung.getStatus());
        assertEquals(0, rechnung.getGesamtbetrag().compareTo(new BigDecimal("350")));
        assertEquals(bestellung.getId(), rechnung.getBestellung().getId());
    }

    @Test
    public void testErstelleRechnungReturnExistingIfAlreadyExists() {
        // Create an order
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos));

        // Create invoice for the first time
        Rechnung rechnung1 = rechnungService.erstelleRechnung(bestellung.getId());
        Long rechnungId = rechnung1.getId();

        // Try to create invoice again for the same order
        Rechnung rechnung2 = rechnungService.erstelleRechnung(bestellung.getId());

        // Should return the same invoice
        assertEquals(rechnungId, rechnung2.getId());
        assertEquals(rechnung1.getRechnungsnummer(), rechnung2.getRechnungsnummer());
    }

    @Test
    public void testErstelleRechnungWithNonExistentBestellung() {
        assertThrows(IllegalArgumentException.class, () -> {
            rechnungService.erstelleRechnung(999999L);
        });
    }

    @Test
    public void testMarkiereAlsBezahlt() {
        // Create order and invoice
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos));
        Rechnung rechnung = rechnungService.erstelleRechnung(bestellung.getId());

        // Mark invoice as paid
        String bearbeiter = "Test Bearbeiter";
        Rechnung bezahlteRechnung = rechnungService.markiereAlsBezahlt(rechnung.getId(), bearbeiter);

        assertEquals(RechnungsStatus.BEZAHLT, bezahlteRechnung.getStatus());
        assertNotNull(bezahlteRechnung.getBezahltAm());
        assertEquals(bearbeiter, bezahlteRechnung.getBezahltVon());

        // Check that order status was updated to ABGESCHLOSSEN
        Bestellung updatedBestellung = bestellungRepository.findById(bestellung.getId()).get();
        assertEquals(BestellStatus.ABGESCHLOSSEN, updatedBestellung.getStatus());
    }

    @Test
    public void testMarkiereAlsBezahltThrowsExceptionIfAlreadyPaid() {
        // Create order and invoice
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos));
        Rechnung rechnung = rechnungService.erstelleRechnung(bestellung.getId());

        // Mark as paid once
        rechnungService.markiereAlsBezahlt(rechnung.getId(), "Bearbeiter 1");

        // Try to mark as paid again
        assertThrows(IllegalStateException.class, () -> {
            rechnungService.markiereAlsBezahlt(rechnung.getId(), "Bearbeiter 2");
        });
    }

    @Test
    public void testMarkiereAlsBezahltWithNonExistentRechnung() {
        assertThrows(IllegalArgumentException.class, () -> {
            rechnungService.markiereAlsBezahlt(999999L, "Test Bearbeiter");
        });
    }

    @Test
    public void testFindeRechnungByBestellungId() {
        // Create order and invoice
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos));
        Rechnung rechnung = rechnungService.erstelleRechnung(bestellung.getId());

        // Find invoice by order ID
        Rechnung foundRechnung = rechnungService.findeRechnungByBestellungId(bestellung.getId());

        assertNotNull(foundRechnung);
        assertEquals(rechnung.getId(), foundRechnung.getId());
    }

    @Test
    public void testFindeRechnungByBestellungIdReturnsNullIfNotExists() {
        // Create order without invoice
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos));

        // Try to find invoice
        Rechnung rechnung = rechnungService.findeRechnungByBestellungId(bestellung.getId());

        assertNull(rechnung);
    }

    @Test
    public void testFindeOffeneRechnungen() {
        // Create multiple orders and invoices
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        
        Bestellung bestellung1 = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));
        Rechnung rechnung1 = rechnungService.erstelleRechnung(bestellung1.getId());

        Bestellung bestellung2 = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));
        Rechnung rechnung2 = rechnungService.erstelleRechnung(bestellung2.getId());

        Bestellung bestellung3 = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));
        Rechnung rechnung3 = rechnungService.erstelleRechnung(bestellung3.getId());

        // Mark one as paid
        rechnungService.markiereAlsBezahlt(rechnung2.getId(), "Test Bearbeiter");

        // Find open invoices
        List<Rechnung> offeneRechnungen = rechnungService.findeOffeneRechnungen();

        // Should have at least 2 open invoices (rechnung1 and rechnung3)
        assertTrue(offeneRechnungen.size() >= 2);
        assertTrue(offeneRechnungen.stream()
                .anyMatch(r -> r.getId().equals(rechnung1.getId())));
        assertTrue(offeneRechnungen.stream()
                .anyMatch(r -> r.getId().equals(rechnung3.getId())));
        assertTrue(offeneRechnungen.stream()
                .noneMatch(r -> r.getId().equals(rechnung2.getId())));
    }

    @Test
    public void testRechnungslauf() {
        // Create multiple orders with status NEU (without invoices)
        BestellPositionDTO pos1 = new BestellPositionDTO(testProdukt1.getId(), 1);
        BestellPositionDTO pos2 = new BestellPositionDTO(testProdukt2.getId(), 2);

        Bestellung bestellung1 = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos1));
        Bestellung bestellung2 = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos2));
        Bestellung bestellung3 = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos1, pos2));

        // Verify orders are in NEU status
        assertEquals(BestellStatus.NEU, bestellung1.getStatus());
        assertEquals(BestellStatus.NEU, bestellung2.getStatus());
        assertEquals(BestellStatus.NEU, bestellung3.getStatus());

        // Run invoice generation batch
        List<Bestellung> bestellungenMitRechnung = rechnungService.rechnungslauf();

        // Should have created invoices for all 3 orders
        assertEquals(3, bestellungenMitRechnung.size());

        // Verify invoices were created
        Rechnung rechnung1 = rechnungService.findeRechnungByBestellungId(bestellung1.getId());
        Rechnung rechnung2 = rechnungService.findeRechnungByBestellungId(bestellung2.getId());
        Rechnung rechnung3 = rechnungService.findeRechnungByBestellungId(bestellung3.getId());

        assertNotNull(rechnung1);
        assertNotNull(rechnung2);
        assertNotNull(rechnung3);

        assertEquals(RechnungsStatus.OFFEN, rechnung1.getStatus());
        assertEquals(RechnungsStatus.OFFEN, rechnung2.getStatus());
        assertEquals(RechnungsStatus.OFFEN, rechnung3.getStatus());

        assertEquals(0, rechnung1.getGesamtbetrag().compareTo(new BigDecimal("100")));
        assertEquals(0, rechnung2.getGesamtbetrag().compareTo(new BigDecimal("100")));
        assertEquals(0, rechnung3.getGesamtbetrag().compareTo(new BigDecimal("200")));
    }

    @Test
    public void testRechnungslaufDoesNotCreateDuplicateInvoices() {
        // Create order
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));

        // Run invoice generation first time
        List<Bestellung> firstRun = rechnungService.rechnungslauf();
        assertTrue(firstRun.size() >= 1);
        assertTrue(firstRun.stream().anyMatch(b -> b.getId().equals(bestellung.getId())));

        Rechnung rechnung1 = rechnungService.findeRechnungByBestellungId(bestellung.getId());
        assertNotNull(rechnung1);
        Long rechnungId = rechnung1.getId();

        // Run invoice generation second time
        List<Bestellung> secondRun = rechnungService.rechnungslauf();
        // Should not include our order anymore since it now has an invoice
        assertTrue(secondRun.stream().noneMatch(b -> b.getId().equals(bestellung.getId())));

        // Verify no duplicate invoice was created
        Rechnung rechnung2 = rechnungService.findeRechnungByBestellungId(bestellung.getId());
        assertEquals(rechnungId, rechnung2.getId());
    }

    @Test
    public void testRechnungslaufOnlyProcessesNeuOrders() {
        // Create orders with different statuses
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);

        Bestellung bestellungNeu = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));

        Bestellung bestellungInBearbeitung = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));
        bestellService.aktualisiereStatus(bestellungInBearbeitung.getId(), BestellStatus.IN_BEARBEITUNG);

        Bestellung bestellungAbgeschlossen = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));
        bestellService.aktualisiereStatus(bestellungAbgeschlossen.getId(), BestellStatus.ABGESCHLOSSEN);

        // Run invoice generation
        List<Bestellung> bestellungenMitRechnung = rechnungService.rechnungslauf();

        // Should only process the NEU order
        assertEquals(1, bestellungenMitRechnung.size());
        assertEquals(bestellungNeu.getId(), bestellungenMitRechnung.get(0).getId());

        // Verify only NEU order has invoice
        assertNotNull(rechnungService.findeRechnungByBestellungId(bestellungNeu.getId()));
        assertNull(rechnungService.findeRechnungByBestellungId(bestellungInBearbeitung.getId()));
        assertNull(rechnungService.findeRechnungByBestellungId(bestellungAbgeschlossen.getId()));
    }

    @Test
    public void testRechnungslaufReturnsEmptyListWhenNoNeuOrders() {
        // Don't create any NEU orders, or create and change their status
        BestellPositionDTO pos = new BestellPositionDTO(testProdukt1.getId(), 1);
        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(), Arrays.asList(pos));
        bestellService.aktualisiereStatus(bestellung.getId(), BestellStatus.IN_BEARBEITUNG);

        // Run invoice generation
        List<Bestellung> bestellungenMitRechnung = rechnungService.rechnungslauf();

        // Should return empty list
        assertEquals(0, bestellungenMitRechnung.size());
    }

    @Test
    public void testGesamtbetragBerechnung() {
        // Create order with multiple positions
        BestellPositionDTO pos1 = new BestellPositionDTO(testProdukt1.getId(), 3);
        BestellPositionDTO pos2 = new BestellPositionDTO(testProdukt2.getId(), 5);

        Bestellung bestellung = bestellService.erstelleBestellung(
                testKunde.getId(),
                Arrays.asList(pos1, pos2));

        Rechnung rechnung = rechnungService.erstelleRechnung(bestellung.getId());

        // Expected: (100 * 3) + (50 * 5) = 300 + 250 = 550
        assertEquals(0, rechnung.getGesamtbetrag().compareTo(new BigDecimal("550")));
    }
}
