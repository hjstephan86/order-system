package com.example.service;

import java.util.List;

import com.example.entity.BestellPosition;
import com.example.entity.BestellStatus;
import com.example.entity.Bestellung;
import com.example.entity.Kunde;
import com.example.entity.Produkt;
import com.example.exception.EntityNotFoundException;
import com.example.exception.InsufficientStockException;
import com.example.repository.BestellungRepository;
import com.example.repository.KundeRepository;
import com.example.repository.ProduktRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Stateless
@Transactional
public class BestellService {

    @Inject
    private BestellungRepository bestellungRepo;

    @Inject
    private KundeRepository kundeRepo;

    @Inject
    private ProduktRepository produktRepo;

    /**
     * Ruft alle Bestellungen ab und initialisiert die lazy-geladene 'positionen'
     * Kollektion.
     * Dadurch wird die LazyInitializationException während der JSON-Serialisierung
     * vermieden.
     */
    public List<Bestellung> findAllAndInitializePositions() {
        List<Bestellung> bestellungen = bestellungRepo.findAll();

        // Explizite Initialisierung der lazy-geladenen Kollektion
        for (Bestellung bestellung : bestellungen) {
            // Durch Aufruf einer Methode auf der Collection wird sie geladen
            bestellung.getPositionen().size();
        }

        return bestellungen;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Bestellung erstelleBestellung(Long kundeId, List<BestellPositionDTO> positionen) {
        Kunde kunde = kundeRepo.findById(kundeId)
                .orElseThrow(() -> new EntityNotFoundException("Kunde", kundeId));

        Bestellung bestellung = new Bestellung();
        bestellung.setKunde(kunde);

        for (BestellPositionDTO dto : positionen) {
            Produkt produkt = produktRepo.findById(dto.getProduktId())
                    .orElseThrow(() -> new EntityNotFoundException("Produkt", dto.getProduktId()));

            if (produkt.getLagerbestand() < dto.getMenge()) {
                throw new InsufficientStockException(
                        produkt.getName(),
                        produkt.getLagerbestand(),
                        dto.getMenge());
            }

            BestellPosition position = new BestellPosition(produkt, dto.getMenge());
            bestellung.addPosition(position);

            // Lagerbestand reduzieren
            produkt.setLagerbestand(produkt.getLagerbestand() - dto.getMenge());
            produktRepo.save(produkt);
        }

        return bestellungRepo.save(bestellung);
    }

    public void aktualisiereStatus(Long bestellungId, BestellStatus neuerStatus) {
        Bestellung bestellung = bestellungRepo.findById(bestellungId)
                .orElseThrow(() -> new RuntimeException("Bestellung nicht gefunden"));
        bestellung.setStatus(neuerStatus);
        bestellungRepo.save(bestellung);
    }
}
