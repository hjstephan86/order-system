package com.example.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produkt")
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String beschreibung;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preis;
    
    @Column(nullable = false)
    private Integer lagerbestand;
    
    // Konstruktoren
    public Produkt() {}
    
    public Produkt(String name, String beschreibung, BigDecimal preis, Integer lagerbestand) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.preis = preis;
        this.lagerbestand = lagerbestand;
    }
    
    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    
    public BigDecimal getPreis() { return preis; }
    public void setPreis(BigDecimal preis) { this.preis = preis; }
    
    public Integer getLagerbestand() { return lagerbestand; }
    public void setLagerbestand(Integer lagerbestand) { this.lagerbestand = lagerbestand; }
}
