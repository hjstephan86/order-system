package com.example.rest;

import java.util.List;

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
        return produktRepository.findById(id)
                .map(existing -> {
                    produkt.setId(id);
                    Produkt updated = produktRepository.save(produkt);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // DELETE /api/produkte/{id} - Produkt löschen
    @DELETE
    @Path("/{id}")
    public Response deleteProdukt(@PathParam("id") Long id) {
        return produktRepository.findById(id)
                .map(produkt -> {
                    produktRepository.delete(produkt);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // GET /api/produkte/suche?name=laptop - Produkte nach Name suchen
    @GET
    @Path("/suche")
    public Response searchProdukte(@QueryParam("name") String name) {
        List<Produkt> produkte = produktRepository.findByNameContaining(name);
        return Response.ok(produkte).build();
    }
}
