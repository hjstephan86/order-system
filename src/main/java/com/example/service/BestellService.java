package com.example.service;

import java.util.List;

import com.example.entity.BestellPosition;
import com.example.entity.BestellStatus;
import com.example.entity.Bestellung;
import com.example.entity.Kunde;
import com.example.entity.Produkt;
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

    public Bestellung erstelleBestellung(Long kundeId, List<BestellPositionDTO> positionen) {
        Kunde kunde = kundeRepo.findById(kundeId)
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));

        Bestellung bestellung = new Bestellung();
        bestellung.setKunde(kunde);

        for (BestellPositionDTO dto : positionen) {
            Produkt produkt = produktRepo.findById(dto.getProduktId())
                    .orElseThrow(() -> new RuntimeException("Produkt nicht gefunden"));

            if (produkt.getLagerbestand() < dto.getMenge()) {
                throw new RuntimeException("Nicht genug Lagerbestand für: " + produkt.getName());
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
