package com.example.rest;

import com.example.entity.Kunde;
import com.example.entity.Produkt;
import com.example.repository.KundeRepository;
import com.example.repository.ProduktRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/testdaten")
@Produces(MediaType.APPLICATION_JSON)
public class TestdatenResource {
    
    @Inject
    private KundeRepository kundeRepository;
    
    @Inject
    private ProduktRepository produktRepository;
    
    // POST /api/testdaten/erstellen - Testdaten erstellen
    @POST
    @Path("/erstellen")
    public Response erstelleTestdaten() {
        // Kunden erstellen
        Kunde kunde1 = new Kunde("Max Mustermann", "max@example.com", "Musterstraße 1, 12345 Berlin");
        Kunde kunde2 = new Kunde("Anna Schmidt", "anna@example.com", "Beispielweg 2, 54321 Hamburg");
        kundeRepository.save(kunde1);
        kundeRepository.save(kunde2);
        
        // Produkte erstellen
        Produkt laptop = new Produkt("Laptop", "Business Laptop mit 16GB RAM", new BigDecimal("899.99"), 10);
        Produkt maus = new Produkt("Maus", "Kabellose Gaming-Maus", new BigDecimal("49.99"), 50);
        Produkt tastatur = new Produkt("Tastatur", "Mechanische RGB-Tastatur", new BigDecimal("129.99"), 25);
        produktRepository.save(laptop);
        produktRepository.save(maus);
        produktRepository.save(tastatur);
        
        return Response.ok()
            .entity(new TestdatenResponse("Testdaten erfolgreich erstellt: 2 Kunden, 3 Produkte"))
            .build();
    }
    
    public static class TestdatenResponse {
        private String message;
        
        public TestdatenResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
