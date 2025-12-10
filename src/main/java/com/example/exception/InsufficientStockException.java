package com.example.exception;

public class InsufficientStockException extends RuntimeException {

    public static final long serialVersionUID = 1L;

    private final String produktName;
    private final int verfuegbar;
    private final int angefordert;

    public InsufficientStockException(String produktName, int verfuegbar, int angefordert) {
        super(String.format("Nicht genug Lagerbestand für: %s (Verfügbar: %d, Angefordert: %d)",
                produktName, verfuegbar, angefordert));
        this.produktName = produktName;
        this.verfuegbar = verfuegbar;
        this.angefordert = angefordert;
    }

    public String getProduktName() {
        return produktName;
    }

    public int getVerfuegbar() {
        return verfuegbar;
    }

    public int getAngefordert() {
        return angefordert;
    }
}