package com.example.rest;

import java.math.BigDecimal;

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
        Kunde kunde1 = new Kunde("Mustermann", "Max", "max@example.com");
        kunde1.setStrasse("Musterstraße");
        kunde1.setHausnummer("1");
        kunde1.setPostleitzahl("12345");
        kunde1.setOrt("Berlin");
        kunde1.setLand("Deutschland");
        kunde1.setTelefonnummer("0301234567");

        Kunde kunde2 = new Kunde("Schmidt", "Anna", "anna@example.com");
        kunde2.setStrasse("Beispielweg");
        kunde2.setHausnummer("2");
        kunde2.setPostleitzahl("54321");
        kunde2.setOrt("Hamburg");
        kunde2.setLand("Deutschland");
        kunde2.setMobilnummer("01761234567");

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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
