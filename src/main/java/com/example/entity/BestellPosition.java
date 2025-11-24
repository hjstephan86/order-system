package com.example.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bestellung_position")
public class BestellPosition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bestellung_id", nullable = false)
    private Bestellung bestellung;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;
    
    @Column(nullable = false)
    private Integer menge;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal einzelpreis;
    
    // Konstruktoren
    public BestellPosition() {}
    
    public BestellPosition(Produkt produkt, Integer menge) {
        this.produkt = produkt;
        this.menge = menge;
        this.einzelpreis = produkt.getPreis();
    }
    
    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Bestellung getBestellung() { return bestellung; }
    public void setBestellung(Bestellung bestellung) { this.bestellung = bestellung; }
    
    public Produkt getProdukt() { return produkt; }
    public void setProdukt(Produkt produkt) { this.produkt = produkt; }
    
    public Integer getMenge() { return menge; }
    public void setMenge(Integer menge) { this.menge = menge; }
    
    public BigDecimal getEinzelpreis() { return einzelpreis; }
    public void setEinzelpreis(BigDecimal einzelpreis) { this.einzelpreis = einzelpreis; }
    
    public BigDecimal getGesamtpreis() {
        return einzelpreis.multiply(new BigDecimal(menge));
    }
}
