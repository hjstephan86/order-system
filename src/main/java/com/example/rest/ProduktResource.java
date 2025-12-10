package com.example.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.example.entity.Produkt;
import com.example.repository.ProduktRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/produkte")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProduktResource {

    @Inject
    private ProduktRepository produktRepository;

    // GET /api/produkte - Alle Produkte abrufen
    @GET
    public Response getAllProdukte() {
        List<Produkt> produkte = produktRepository.findAll();
        return Response.ok(produkte).build();
    }

    // GET /api/produkte/{id} - Einzelnes Produkt abrufen
    @GET
    @Path("/{id}")
    public Response getProdukt(@PathParam("id") Long id) {
        return produktRepository.findById(id)
                .map(produkt -> Response.ok(produkt).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // POST /api/produkte - Neues Produkt anlegen
    @POST
    public Response createProdukt(Produkt produkt) {
        Produkt saved = produktRepository.save(produkt);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    // PUT /api/produkte/{id} - Produkt aktualisieren
    @PUT
    @Path("/{id}")
    public Response updateProdukt(@PathParam("id") Long id, Produkt produkt) {
        try {
            return produktRepository.findById(id)
                    .map(existing -> {
                        produkt.setId(id);
                        Produkt updated = produktRepository.save(produkt);
                        return Response.ok(updated).build();
                    })
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(new HashMap<String, String>() {
                                {
                                    put("message", "Produkt nicht gefunden");
                                }
                            })
                            .build());
        } catch (Exception e) {
            String errorMsg = getFullErrorMessage(e);

            if (errorMsg.contains("name") && (errorMsg.contains("unique") || errorMsg.contains("duplicate"))) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Ein Produkt mit diesem Namen existiert bereits");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("unique") || errorMsg.contains("duplicate")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Ein Produkt mit diesen Daten existiert bereits");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("constraint")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Die Produktdaten verletzen eine Datenbankregel");
                            }
                        })
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Fehler beim Aktualisieren des Produkts: " + e.getMessage());
                            }
                        })
                        .build();
            }
        }
    }

    // DELETE /api/produkte/{id} - Produkt löschen
    @DELETE
    @Path("/{id}")
    public Response deleteProdukt(@PathParam("id") Long id) {
        try {
            Optional<Produkt> produkt = produktRepository.findById(id);
            if (produkt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Produkt nicht gefunden");
                            }
                        })
                        .build();
            }

            produktRepository.delete(produkt.get());
            return Response.noContent().build();

        } catch (Exception e) {
            String errorMsg = getFullErrorMessage(e);

            if (errorMsg.contains("bestellposition") && errorMsg.contains("foreign key")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message",
                                        "Produkt kann nicht gelöscht werden, da es in Bestellungen verwendet wird");
                                put("details", "Bitte löschen Sie zuerst alle Bestellungen mit diesem Produkt");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("foreign key") || errorMsg.contains("violates")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Produkt kann nicht gelöscht werden, da noch Abhängigkeiten bestehen");
                                put("details", "Das Produkt wird noch von anderen Datensätzen referenziert");
                            }
                        })
                        .build();
            } else if (errorMsg.contains("constraintviolationexception")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Produkt kann nicht gelöscht werden (Datenbankregel verletzt)");
                            }
                        })
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new HashMap<String, String>() {
                            {
                                put("message", "Fehler beim Löschen des Produkts: " + e.getMessage());
                            }
                        })
                        .build();
            }
        }
    }

    // GET /api/produkte/suche?name=laptop - Produkte nach Name suchen
    @GET
    @Path("/suche")
    public Response searchProdukte(@QueryParam("name") String name) {
        List<Produkt> produkte = produktRepository.findByNameContaining(name);
        return Response.ok(produkte).build();
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
}
