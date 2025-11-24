// Datei: com/example/service/BestellPositionDTO.java
package com.example.service;

public class BestellPositionDTO {
    private Long produktId;
    private Integer menge;

    // Standard-Konstruktor ist notwendig für Jackson
    public BestellPositionDTO() {
    }

    public BestellPositionDTO(Long produktId, Integer menge) {
        this.produktId = produktId;
        this.menge = menge;
    }

    // Getter
    public Long getProduktId() {
        return produktId;
    }

    public Integer getMenge() {
        return menge;
    }

    // Setter
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    public void setMenge(Integer menge) {
        this.menge = menge;
    }
}
