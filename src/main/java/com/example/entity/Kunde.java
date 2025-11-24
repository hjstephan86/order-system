package com.example.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "kunde")
public class Kunde {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 200)
    private String adresse;
    
    @JsonIgnore
    @OneToMany(mappedBy = "kunde", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bestellung> bestellungen = new ArrayList<>();
    
    // Konstruktoren
    public Kunde() {}
    
    public Kunde(String name, String email, String adresse) {
        this.name = name;
        this.email = email;
        this.adresse = adresse;
    }
    
    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public List<Bestellung> getBestellungen() { return bestellungen; }
    public void setBestellungen(List<Bestellung> bestellungen) { this.bestellungen = bestellungen; }
    
    // Hilfsmethode
    public void addBestellung(Bestellung bestellung) {
        bestellungen.add(bestellung);
        bestellung.setKunde(this);
    }
}
