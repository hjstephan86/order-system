package com.example.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RechnungDTO {
    private Long id;
    private String rechnungsnummer;
    private Long bestellungId;
    private String kundeName;
    private LocalDateTime erstellungsdatum;
    private BigDecimal gesamtbetrag;
    private String status;
    private LocalDateTime bezahltAm;
    private String bezahltVon;

    // Constructor
    public RechnungDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(String rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public Long getBestellungId() {
        return bestellungId;
    }

    public void setBestellungId(Long bestellungId) {
        this.bestellungId = bestellungId;
    }

    public String getKundeName() {
        return kundeName;
    }

    public void setKundeName(String kundeName) {
        this.kundeName = kundeName;
    }

    public LocalDateTime getErstellungsdatum() {
        return erstellungsdatum;
    }

    public void setErstellungsdatum(LocalDateTime erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public BigDecimal getGesamtbetrag() {
        return gesamtbetrag;
    }

    public void setGesamtbetrag(BigDecimal gesamtbetrag) {
        this.gesamtbetrag = gesamtbetrag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBezahltAm() {
        return bezahltAm;
    }

    public void setBezahltAm(LocalDateTime bezahltAm) {
        this.bezahltAm = bezahltAm;
    }

    public String getBezahltVon() {
        return bezahltVon;
    }

    public void setBezahltVon(String bezahltVon) {
        this.bezahltVon = bezahltVon;
    }
}