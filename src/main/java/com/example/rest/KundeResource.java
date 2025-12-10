package com.example.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.example.entity.Kunde;
import com.example.repository.KundeRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/kunden")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KundeResource {

    @Inject
    private KundeRepository kundeRepository;

    // GET /api/kunden - Alle Kunden abrufen
    @GET
    public Response getAllKunden() {
        List<Kunde> kunden = kundeRepository.findAll();
        return Response.ok(kunden).build();
    }

    // GET /api/kunden/{id} - Einzelnen Kunden abrufen
    @GET
    @Path("/{id}")
    public Response getKunde(@PathParam("id") Long id) {
        return kundeRepository.findById(id)
                .map(kunde -> Response.ok(kunde).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // POST /api/kunden - Neuen Kunden anlegen
    @POST
    public Response createKunde(Kunde kunde) {
        Kunde saved = kundeRepository.save(kunde);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    // PUT /api/kunden/{id} - Kunden aktualisieren
    @PUT
    @Path("/{id}")
    public Response updateKunde(@PathParam("id") Long id, Kunde kunde) {
        try {
            return kundeRepository.findById(id)
                    .map(existing -> {
                        kunde.setId(id);
                        Kunde updated = kundeRepository.save(kunde);
                        return Response.ok(updated).build();
                    })
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(new HashMap<String, String>() {
                                {
                                    put("message", "Kunde nicht gefunden");
                                }
                            })
                            .build());
        } catch (Exception e) {
            return handleUpdateKundeException(e);
        }
    }

    // DELETE /api/kunden/{id} - Kunde löschen
    @DELETE
    @Path("/{id}")
    public Response deleteKunde(@PathParam("id") Long id) {
        try {
            Optional<Kunde> kunde = kundeRepository.findById(id);
            if (kunde.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Kunde nicht gefunden");
                            }
                        })
                        .build();
            }

            kundeRepository.delete(kunde.get());
            return Response.noContent().build();

        } catch (Exception e) {
            return handleDeleteKundeException(e);
        }
    }

    // GET /api/kunden/email/{email} - Kunde per Email suchen
    @GET
    @Path("/email/{email}")
    public Response getKundeByEmail(@PathParam("email") String email) {
        return kundeRepository.findByEmail(email)
                .map(kunde -> Response.ok(kunde).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // Hilfsmethode um die komplette Exception-Kette zu durchsuchen
    private String getFullErrorMessage(Throwable e) {
        StringBuilder sb = new StringBuilder();
        Throwable current = e;
        while (current != null) {
            if (current.getMessage() != null) {
                sb.append(current.getMessage()).append(' ');
            }
            sb.append(current.getClass().getName()).append(' ');
            current = current.getCause();
        }
        return sb.toString().toLowerCase();
    }

    private Response handleUpdateKundeException(Exception e) {
        String errorMsg = getFullErrorMessage(e);

        if (isUniqueConstraintViolation(errorMsg)) {
            return handleUniqueViolation(errorMsg);
        } else if (errorMsg.contains("constraint")) {
            return createBadRequestResponse("Die Kundendaten verletzen eine Datenbankregel");
        } else {
            return createServerErrorResponse("Fehler beim Aktualisieren des Kunden: " + e.getMessage());
        }
    }

    private Response handleUniqueViolation(String errorMsg) {
        if (errorMsg.contains("email")) {
            return createConflictResponse("Die E-Mail-Adresse wird bereits von einem anderen Kunden verwendet");
        }
        return createConflictResponse("Ein Kunde mit diesen Daten existiert bereits");
    }

    private boolean isUniqueConstraintViolation(String errorMsg) {
        return errorMsg.contains("unique") || errorMsg.contains("duplicate");
    }

    private Response createConflictResponse(String message) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new HashMap<String, String>() {
                    {
                        put("message", message);
                    }
                })
                .build();
    }

    private Response createBadRequestResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new HashMap<String, String>() {
                    {
                        put("message", message);
                    }
                })
                .build();
    }

    private Response createServerErrorResponse(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new HashMap<String, String>() {
                    {
                        put("message", message);
                    }
                })
                .build();
    }

    private Response handleDeleteKundeException(Exception e) {
        String errorMsg = getFullErrorMessage(e);

        if (errorMsg.contains("bestellung") && errorMsg.contains("foreign key")) {
            return createConflictResponseWithDetails(
                    "Kunde kann nicht gelöscht werden, da noch Bestellungen existieren",
                    "Bitte löschen Sie zuerst alle Bestellungen dieses Kunden");
        } else if (errorMsg.contains("rechnung") && errorMsg.contains("foreign key")) {
            return createConflictResponseWithDetails(
                    "Kunde kann nicht gelöscht werden, da noch Rechnungen existieren",
                    "Bitte löschen Sie zuerst alle Rechnungen dieses Kunden");
        } else if (errorMsg.contains("foreign key") || errorMsg.contains("violates")) {
            return createConflictResponseWithDetails(
                    "Kunde kann nicht gelöscht werden, da noch Abhängigkeiten bestehen",
                    "Der Kunde wird noch von anderen Datensätzen referenziert");
        } else if (errorMsg.contains("constraintviolationexception")) {
            return createConflictResponse("Kunde kann nicht gelöscht werden (Datenbankregel verletzt)");
        } else {
            return createServerErrorResponse("Fehler beim Löschen des Kunden: " + e.getMessage());
        }
    }

    private Response createConflictResponseWithDetails(String message, String details) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new HashMap<String, String>() {
                    {
                        put("message", message);
                        put("details", details);
                    }
                })
                .build();
    }
}
