package com.example.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bestellung")
public class Bestellung {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;
    
    @Column(nullable = false)
    private LocalDateTime bestelldatum;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BestellStatus status;
    
    @OneToMany(mappedBy = "bestellung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BestellPosition> positionen = new ArrayList<>();
    
    // Konstruktoren
    public Bestellung() {
        this.bestelldatum = LocalDateTime.now();
        this.status = BestellStatus.NEU;
    }
    
    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Kunde getKunde() { return kunde; }
    public void setKunde(Kunde kunde) { this.kunde = kunde; }
    
    public LocalDateTime getBestelldatum() { return bestelldatum; }
    public void setBestelldatum(LocalDateTime bestelldatum) { this.bestelldatum = bestelldatum; }
    
    public BestellStatus getStatus() { return status; }
    public void setStatus(BestellStatus status) { this.status = status; }
    
    public List<BestellPosition> getPositionen() { return positionen; }
    public void setPositionen(List<BestellPosition> positionen) { this.positionen = positionen; }
    
    // Hilfsmethoden
    public void addPosition(BestellPosition position) {
        positionen.add(position);
        position.setBestellung(this);
    }
    
    public BigDecimal getGesamtpreis() {
        return positionen.stream()
            .map(BestellPosition::getGesamtpreis)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
