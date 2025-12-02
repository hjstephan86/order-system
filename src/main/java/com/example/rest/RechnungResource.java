package com.example.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.entity.Rechnung;
import com.example.service.RechnungService;

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

@Path("/rechnungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RechnungResource {

    @Inject
    private RechnungService rechnungService;

    private RechnungDTO toDTO(Rechnung rechnung) {
        RechnungDTO dto = new RechnungDTO();
        dto.setId(rechnung.getId());
        dto.setRechnungsnummer(rechnung.getRechnungsnummer());
        dto.setBestellungId(rechnung.getBestellung().getId());
        dto.setKundeName(rechnung.getBestellung().getKunde().getName());
        dto.setErstellungsdatum(rechnung.getErstellungsdatum());
        dto.setGesamtbetrag(rechnung.getGesamtbetrag());
        dto.setStatus(rechnung.getStatus().toString());
        dto.setBezahltAm(rechnung.getBezahltAm());
        dto.setBezahltVon(rechnung.getBezahltVon());
        return dto;
    }

    @GET
    public Response getAlleRechnungen() {
        try {
            List<Rechnung> rechnungen = rechnungService.findeAlleRechnungen();
            List<RechnungDTO> dtos = rechnungen.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getRechnungById(@PathParam("id") Long id) {
        try {
            Rechnung rechnung = rechnungService.findeRechnungById(id);
            return Response.ok(toDTO(rechnung)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/bestellung/{bestellungId}")
    public Response getRechnungByBestellungId(@PathParam("bestellungId") Long bestellungId) {
        try {
            Rechnung rechnung = rechnungService.findeRechnungByBestellungId(bestellungId);
            if (rechnung == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Keine Rechnung für diese Bestellung gefunden"))
                        .build();
            }
            return Response.ok(toDTO(rechnung)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/offen")
    public Response getOffeneRechnungen() {
        try {
            List<Rechnung> rechnungen = rechnungService.findeOffeneRechnungen();
            List<RechnungDTO> dtos = rechnungen.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/erstellen/{bestellungId}")
    public Response erstelleRechnung(@PathParam("bestellungId") Long bestellungId) {
        try {
            Rechnung rechnung = rechnungService.erstelleRechnung(bestellungId);
            return Response.status(Response.Status.CREATED).entity(toDTO(rechnung)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/bezahlen")
    public Response markiereAlsBezahlt(
            @PathParam("id") Long id,
            Map<String, String> requestBody) {
        try {
            String bearbeiter = requestBody.getOrDefault("bearbeiter", "System");
            Rechnung rechnung = rechnungService.markiereAlsBezahlt(id, bearbeiter);
            return Response.ok(toDTO(rechnung)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}/pdf")
    public Response downloadPdf(@PathParam("id") Long id) {
        try {
            byte[] pdfBytes = rechnungService.generiereRechnungsPdf(id);
            // We need the invoice number for filename — fetch lightweight DTO
            Rechnung rechnung = rechnungService.findeRechnungById(id);
            return Response.ok(pdfBytes)
                    .header("Content-Disposition",
                            "attachment; filename=\"" + rechnung.getRechnungsnummer() + ".pdf\"")
                    .header("Content-Type", "application/pdf")
                    .build();
        } catch (IllegalArgumentException e) {
            System.err.println("Rechnung nicht gefunden: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Rechnung nicht gefunden")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (Exception e) {
            System.err.println("Fehler beim PDF-Generieren: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Fehler beim Generieren des PDFs: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}