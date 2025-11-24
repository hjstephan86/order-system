package com.example.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // Konfiguriert den Basis-Pfad für alle REST-Endpoints
    // Alle REST-Services sind unter: http://localhost:8080/order-system/api/...
}
