package com.example.service;

import com.example.entity.Rechnung;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Test stub for PdfService to avoid bringing iText into the Arquillian test
 * deployment.
 * This class is placed under src/test so it will replace the main
 * implementation in the
 * test deployment and prevents Weld from failing due to missing iText classes.
 */
@ApplicationScoped
public class PdfService {

    public byte[] generiereRechnungsPdf(Rechnung rechnung) throws Exception {
        // Return a lightweight placeholder PDF-like byte array for integration tests.
        // Tests that need a real PDF can be adjusted to include iText in the container.
        String placeholder = "PDF-PLACEHOLDER: Rechnung "
                + (rechnung != null && rechnung.getId() != null ? rechnung.getId() : "-") + "\n";
        return placeholder.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
