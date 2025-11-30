package com.example.entity;

/**
 * Status enum for orders (Bestellung)
 * Extended to support payment workflow
 */
public enum BestellStatus {
    NEU, // New order created
    IN_BEARBEITUNG, // Order is being processed
    VERSENDET, // Order has been shipped
    ABGESCHLOSSEN, // Order is completed and paid
    STORNIERT // Order is cancelled
}
