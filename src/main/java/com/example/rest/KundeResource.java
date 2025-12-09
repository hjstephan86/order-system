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
            String errorMsg = getFullErrorMessage(e);

            // Spezifische Fehlerunterscheidung
            if (errorMsg.contains("email") && (errorMsg.contains("unique") || errorMsg.contains("duplicate"))) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Die E-Mail-Adresse wird bereits von einem anderen Kunden verwendet");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("name") && (errorMsg.contains("unique") || errorMsg.contains("duplicate"))) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Ein Kunde mit diesem Namen existiert bereits");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("unique") || errorMsg.contains("duplicate")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Ein Kunde mit diesen Daten existiert bereits");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("constraint")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Die Kundendaten verletzen eine Datenbankregel");
                            }
                        })
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Fehler beim Aktualisieren des Kunden: " + e.getMessage());
                            }
                        })
                        .build();
            }
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
            String errorMsg = getFullErrorMessage(e);

            // Spezifische Fehlerunterscheidung
            if (errorMsg.contains("bestellung") && errorMsg.contains("foreign key")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Kunde kann nicht gelöscht werden, da noch Bestellungen existieren");
                                put("details", "Bitte löschen Sie zuerst alle Bestellungen dieses Kunden");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("rechnung") && errorMsg.contains("foreign key")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Kunde kann nicht gelöscht werden, da noch Rechnungen existieren");
                                put("details", "Bitte löschen Sie zuerst alle Rechnungen dieses Kunden");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("foreign key") || errorMsg.contains("violates")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Kunde kann nicht gelöscht werden, da noch Abhängigkeiten bestehen");
                                put("details", "Der Kunde wird noch von anderen Datensätzen referenziert");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("constraintviolationexception")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Kunde kann nicht gelöscht werden (Datenbankregel verletzt)");
                            }
                        })
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Fehler beim Löschen des Kunden: " + e.getMessage());
                            }
                        })
                        .build();
            }
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
                sb.append(current.getMessage()).append(" ");
            }
            sb.append(current.getClass().getName()).append(" ");
            current = current.getCause();
        }
        return sb.toString().toLowerCase();
    }
}
