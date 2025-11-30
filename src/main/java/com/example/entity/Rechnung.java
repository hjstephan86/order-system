package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rechnung")
public class Rechnung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rechnungsnummer;

    @OneToOne
    @JoinColumn(name = "bestellung_id", nullable = false)
    private Bestellung bestellung;

    @Column(nullable = false)
    private LocalDateTime erstellungsdatum;

    @Column(nullable = false)
    private BigDecimal gesamtbetrag;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RechnungsStatus status = RechnungsStatus.OFFEN;

    private LocalDateTime bezahltAm;

    private String bezahltVon; // Username/System user who marked as paid

    // Constructors
    public Rechnung() {
        this.erstellungsdatum = LocalDateTime.now();
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

    public Bestellung getBestellung() {
        return bestellung;
    }

    public void setBestellung(Bestellung bestellung) {
        this.bestellung = bestellung;
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

    public RechnungsStatus getStatus() {
        return status;
    }

    public void setStatus(RechnungsStatus status) {
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
