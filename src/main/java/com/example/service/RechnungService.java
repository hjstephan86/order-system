package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.example.entity.BestellPosition;
import com.example.entity.BestellStatus;
import com.example.entity.Bestellung;
import com.example.entity.Rechnung;
import com.example.entity.RechnungsStatus;
import com.example.repository.BestellungRepository;
import com.example.repository.RechnungRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RechnungService {

    @Inject
    private RechnungRepository rechnungRepository;

    @Inject
    private PdfService pdfService;

    @Inject
    private BestellungRepository bestellungRepository;

    @Transactional
    public Rechnung erstelleRechnung(Long bestellungId) {
        Bestellung bestellung = bestellungRepository.findById(bestellungId)
                .orElseThrow(() -> new IllegalArgumentException("Bestellung nicht gefunden"));

        // Check if bill already exists
        Rechnung existingRechnung = rechnungRepository.findByBestellungId(bestellungId);
        if (existingRechnung != null) {
            return existingRechnung;
        }

        Rechnung rechnung = new Rechnung();
        rechnung.setBestellung(bestellung);
        rechnung.setRechnungsnummer(generiereRechnungsnummer());
        rechnung.setGesamtbetrag(berechneGesamtbetrag(bestellung));
        rechnung.setStatus(RechnungsStatus.OFFEN);

        return rechnungRepository.save(rechnung);
    }

    @Transactional
    public Rechnung markiereAlsBezahlt(Long rechnungId, String bearbeiter) {
        Rechnung rechnung = rechnungRepository.findById(rechnungId)
                .orElseThrow(() -> new IllegalArgumentException("Rechnung nicht gefunden"));

        if (rechnung.getStatus() == RechnungsStatus.BEZAHLT) {
            throw new IllegalStateException("Rechnung ist bereits als bezahlt markiert");
        }

        rechnung.setStatus(RechnungsStatus.BEZAHLT);
        rechnung.setBezahltAm(LocalDateTime.now());
        rechnung.setBezahltVon(bearbeiter);

        // Update order status to ABGESCHLOSSEN (completed)
        Bestellung bestellung = rechnung.getBestellung();
        bestellung.setStatus(BestellStatus.ABGESCHLOSSEN);
        bestellungRepository.save(bestellung);

        return rechnungRepository.save(rechnung);
    }

    public List<Rechnung> findeAlleRechnungen() {
        return rechnungRepository.findAll();
    }

    public Rechnung findeRechnungById(Long id) {
        return rechnungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rechnung nicht gefunden"));
    }

    @Transactional
    public byte[] generiereRechnungsPdf(Long id) throws Exception {
        Rechnung rechnung = rechnungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rechnung nicht gefunden"));

        // Ensure lazy associations are initialized while the persistence context /
        // transaction is active
        if (rechnung.getBestellung() != null) {
            // touch fields to initialize
            rechnung.getBestellung().getKunde().getName();
            rechnung.getBestellung().getPositionen().size();
            rechnung.getBestellung().getPositionen().forEach(p -> p.getProdukt().getName());
        }

        return pdfService.generiereRechnungsPdf(rechnung);
    }

    public Rechnung findeRechnungByBestellungId(Long bestellungId) {
        return rechnungRepository.findByBestellungId(bestellungId);
    }

    public List<Rechnung> findeOffeneRechnungen() {
        return rechnungRepository.findByStatus(RechnungsStatus.OFFEN);
    }

    public List<Bestellung> rechnungslauf() {
        List<Bestellung> neueBestellungen = bestellungRepository.findByStatus(BestellStatus.NEU);
        List<Bestellung> neueBestellungenMitRechnung = new ArrayList<Bestellung>();
        for (Bestellung bestellung : neueBestellungen) {
            Long bestellungId = bestellung.getId();
            Rechnung existingRechnung = rechnungRepository.findByBestellungId(bestellungId);
            if (existingRechnung == null) {
                Rechnung rechnung = new Rechnung();
                rechnung.setBestellung(bestellung);
                rechnung.setRechnungsnummer(generiereRechnungsnummer());
                rechnung.setGesamtbetrag(berechneGesamtbetrag(bestellung));
                rechnung.setStatus(RechnungsStatus.OFFEN);
                rechnungRepository.save(rechnung);
                neueBestellungenMitRechnung.add(bestellung);
            }
        }
        return neueBestellungenMitRechnung;
    }

    private String generiereRechnungsnummer() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "RE-" + timestamp + "-" + randomNumber;
    }

    @Transactional
    private BigDecimal berechneGesamtbetrag(Bestellung bestellung) {
        if (bestellung == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal gesamtbetrag = BigDecimal.ZERO;

        List<BestellPosition> positionen = bestellung.getPositionen();

        if (positionen == null || positionen.isEmpty()) {
            return BigDecimal.ZERO;
        }

        for (BestellPosition pos : positionen) {
            if (pos == null) {
                continue;
            }

            BigDecimal einzelpreis = pos.getEinzelpreis();
            if (einzelpreis == null) {
                continue;
            }

            Integer menge = pos.getMenge();
            if (menge == null) {
                continue;
            }

            BigDecimal positionsBetrag = einzelpreis.multiply(BigDecimal.valueOf(menge));
            gesamtbetrag = gesamtbetrag.add(positionsBetrag);
        }

        return gesamtbetrag;
    }
}
