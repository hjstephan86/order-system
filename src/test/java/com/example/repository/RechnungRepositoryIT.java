package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.entity.Bestellung;
import com.example.entity.Kunde;
import com.example.entity.Rechnung;
import com.example.entity.RechnungsStatus;

import jakarta.inject.Inject;

@ExtendWith(ArquillianExtension.class)
public class RechnungRepositoryIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.example.entity")
                .addPackages(true, "com.example.repository")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private KundeRepository kundeRepository;

    @Inject
    private BestellungRepository bestellungRepository;

    @Inject
    private RechnungRepository rechnungRepository;

    @Test
    public void testSaveFindUpdateAndDeleteRechnung() {
        // create and persist a customer
        Kunde kunde = new Kunde("Test Kunde", "test@kunde.local", "Some Street 1");
        Kunde savedKunde = kundeRepository.save(kunde);
        assertNotNull(savedKunde.getId());

        // create and persist a Bestellung linked to the customer
        Bestellung bestellung = new Bestellung();
        bestellung.setKunde(savedKunde);
        Bestellung savedBestellung = bestellungRepository.save(bestellung);
        assertNotNull(savedBestellung.getId());

        // create and persist a Rechnung linked to the Bestellung
        Rechnung rechnung = new Rechnung();
        rechnung.setRechnungsnummer("INV-IT-1");
        rechnung.setBestellung(savedBestellung);
        rechnung.setGesamtbetrag(new BigDecimal("42.50"));

        Rechnung saved = rechnungRepository.save(rechnung);
        assertNotNull(saved.getId());
        assertEquals("INV-IT-1", saved.getRechnungsnummer());

        // find by id
        Rechnung found = rechnungRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(saved.getRechnungsnummer(), found.getRechnungsnummer());

        // find by bestellung id
        Rechnung byBestellung = rechnungRepository.findByBestellungId(savedBestellung.getId());
        assertNotNull(byBestellung);
        assertEquals(saved.getId(), byBestellung.getId());

        // find by status (default OFFEN)
        List<Rechnung> offen = rechnungRepository.findByStatus(RechnungsStatus.OFFEN);
        assertTrue(offen.stream().anyMatch(r -> r.getId().equals(saved.getId())));

        // update status to BEZAHLT and save
        saved.setStatus(RechnungsStatus.BEZAHLT);
        rechnungRepository.save(saved);

        List<Rechnung> bezahlt = rechnungRepository.findByStatus(RechnungsStatus.BEZAHLT);
        assertTrue(bezahlt.stream().anyMatch(r -> r.getId().equals(saved.getId())));

        // delete and verify
        rechnungRepository.delete(saved);
        Rechnung afterDelete = rechnungRepository.findById(saved.getId()).orElse(null);
        assertNull(afterDelete);
    }

    @Test
    public void testFindAllReturnsList() {
        // Ensure at least one Rechnung exists
        Kunde k = new Kunde("Another", "another@local", "Addr");
        Kunde sk = kundeRepository.save(k);
        Bestellung b = new Bestellung();
        b.setKunde(sk);
        Bestellung sb = bestellungRepository.save(b);

        Rechnung r = new Rechnung();
        r.setRechnungsnummer("INV-IT-2");
        r.setBestellung(sb);
        r.setGesamtbetrag(new BigDecimal("10.00"));
        rechnungRepository.save(r);

        List<Rechnung> all = rechnungRepository.findAll();
        assertTrue(all.size() >= 1);
    }
}
