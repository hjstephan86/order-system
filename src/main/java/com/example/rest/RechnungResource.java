package com.example.rest;

import java.util.List;
import java.util.Map;

import com.example.entity.Rechnung;
import com.example.service.PdfService;
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

    @Inject
    private PdfService pdfService;

    @GET
    public Response getAlleRechnungen() {
        try {
            List<Rechnung> rechnungen = rechnungService.findeAlleRechnungen();
            return Response.ok(rechnungen).build();
        } catch (Exception e) {
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
            return Response.ok(rechnung).build();
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
            return Response.ok(rechnung).build();
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
            return Response.ok(rechnungen).build();
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
            return Response.status(Response.Status.CREATED).entity(rechnung).build();
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
            return Response.ok(rechnung).build();
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
    @Produces("application/pdf")
    public Response downloadPdf(@PathParam("id") Long id) {
        try {
            Rechnung rechnung = rechnungService.findeRechnungById(id);
            byte[] pdfBytes = pdfService.generiereRechnungsPdf(rechnung);

            return Response.ok(pdfBytes)
                    .header("Content-Disposition",
                            "attachment; filename=\"" + rechnung.getRechnungsnummer() + ".pdf\"")
                    .build();
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
}
