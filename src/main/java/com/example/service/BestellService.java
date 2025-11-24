package com.example.service;

import com.example.entity.*;
import com.example.repository.*;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

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
    
    // DTO für Bestellung erstellen
    public static class BestellPositionDTO {
        private Long produktId;
        private Integer menge;
        
        public BestellPositionDTO(Long produktId, Integer menge) {
            this.produktId = produktId;
            this.menge = menge;
        }
        
        public Long getProduktId() { return produktId; }
        public Integer getMenge() { return menge; }
    }
}
