package com.example.rest;

import java.util.List;

import com.example.entity.BestellStatus;
import com.example.entity.Bestellung;
import com.example.repository.BestellungRepository;
import com.example.service.BestellPositionDTO;
import com.example.service.BestellService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bestellungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BestellungResource {

    @Inject
    private BestellungRepository bestellungRepository;

    @Inject
    private BestellService bestellService;

    // GET /api/bestellungen - Alle Bestellungen abrufen
    @GET
    public Response getAllBestellungen() {
        // FIX: Nutzen Sie den Service, um Bestellungen abzurufen und die
        // lazy-Kollektionen zu initialisieren
        List<Bestellung> bestellungen = bestellService.findAllAndInitializePositions();
        return Response.ok(bestellungen).build();
    }

    // GET /api/bestellungen/{id} - Einzelne Bestellung abrufen
    @GET
    @Path("/{id}")
    public Response getBestellung(@PathParam("id") Long id) {
        // FIX: Rufen Sie die Methode im Service auf, die die Initialisierung
        // durchführt.
        List<Bestellung> bestellungen = bestellService.findAllAndInitializePositions();
        return Response.ok(bestellungen).build();
    }

    // POST /api/bestellungen - Neue Bestellung erstellen
    @POST
    public Response createBestellung(BestellungCreateRequest request) {
        try {
            Bestellung bestellung = bestellService.erstelleBestellung(
                    request.getKundeId(),
                    request.getPositionen());
            return Response.status(Response.Status.CREATED).entity(bestellung).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/bestellungen/{id}/status - Status einer Bestellung ändern
    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, StatusUpdateRequest request) {
        try {
            bestellService.aktualisiereStatus(id, request.getStatus());
            return bestellungRepository.findById(id)
                    .map(bestellung -> Response.ok(bestellung).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // GET /api/bestellungen/kunde/{kundeId} - Bestellungen eines Kunden
    @GET
    @Path("/kunde/{kundeId}")
    public Response getBestellungenByKunde(@PathParam("kundeId") Long kundeId) {
        List<Bestellung> bestellungen = bestellungRepository.findByKundeId(kundeId);
        return Response.ok(bestellungen).build();
    }

    // GET /api/bestellungen/status/{status} - Bestellungen nach Status
    @GET
    @Path("/status/{status}")
    public Response getBestellungenByStatus(@PathParam("status") BestellStatus status) {
        List<Bestellung> bestellungen = bestellungRepository.findByStatus(status);
        return Response.ok(bestellungen).build();
    }

    // DTO für Bestellung erstellen
    public static class BestellungCreateRequest {
        private Long kundeId;
        private List<BestellPositionDTO> positionen;

        public Long getKundeId() {
            return kundeId;
        }

        public void setKundeId(Long kundeId) {
            this.kundeId = kundeId;
        }

        public List<BestellPositionDTO> getPositionen() {
            return positionen;
        }

        public void setPositionen(List<BestellPositionDTO> positionen) {
            this.positionen = positionen;
        }
    }

    // DTO für Status-Update
    public static class StatusUpdateRequest {
        private BestellStatus status;

        public BestellStatus getStatus() {
            return status;
        }

        public void setStatus(BestellStatus status) {
            this.status = status;
        }
    }

    // Error Response DTO
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
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
