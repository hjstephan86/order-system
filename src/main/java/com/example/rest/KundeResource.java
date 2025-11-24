package com.example.rest;

import com.example.entity.Kunde;
import com.example.repository.KundeRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

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
        return kundeRepository.findById(id)
            .map(existing -> {
                kunde.setId(id);
                Kunde updated = kundeRepository.save(kunde);
                return Response.ok(updated).build();
            })
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    // DELETE /api/kunden/{id} - Kunden löschen
    @DELETE
    @Path("/{id}")
    public Response deleteKunde(@PathParam("id") Long id) {
        return kundeRepository.findById(id)
            .map(kunde -> {
                kundeRepository.delete(kunde);
                return Response.noContent().build();
            })
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    // GET /api/kunden/email/{email} - Kunde per Email suchen
    @GET
    @Path("/email/{email}")
    public Response getKundeByEmail(@PathParam("email") String email) {
        return kundeRepository.findByEmail(email)
            .map(kunde -> Response.ok(kunde).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
