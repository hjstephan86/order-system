package com.example.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.example.entity.BestellPosition;
import com.example.entity.Rechnung;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public byte[] generiereRechnungsPdf(Rechnung rechnung) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Header
            addHeader(document, rechnung);
            document.add(Chunk.NEWLINE);

            // Customer info
            addKundenInfo(document, rechnung);
            document.add(Chunk.NEWLINE);

            // Invoice details
            addRechnungsDetails(document, rechnung);
            document.add(Chunk.NEWLINE);

            // Items table
            addPositionenTabelle(document, rechnung);
            document.add(Chunk.NEWLINE);

            // Total
            addGesamtsumme(document, rechnung);
            document.add(Chunk.NEWLINE);

            // Footer
            addFooter(document);

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    private void addHeader(Document document, Rechnung rechnung) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph("RECHNUNG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        Paragraph company = new Paragraph("Ihr Firmenname\nMusterstraße 123\n12345 Musterstadt", normalFont);
        company.setAlignment(Element.ALIGN_LEFT);
        document.add(company);
    }

    private void addKundenInfo(Document document, Rechnung rechnung) throws DocumentException {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph kundenInfo = new Paragraph();
        kundenInfo.add(new Chunk("An:\n", boldFont));
        if (rechnung.getBestellung().getKunde().getGeschlecht() != null) {
            switch (rechnung.getBestellung().getKunde().getGeschlecht()) {
                case MAENNLICH:
                    kundenInfo.add(new Chunk("Herrn ", normalFont));
                    break;
                case WEIBLICH:
                    kundenInfo.add(new Chunk("Frau ", normalFont));
                    break;
                case DIVERS:
                    kundenInfo.add(new Chunk("", normalFont));
                    break;
            }
        }
        kundenInfo.add(new Chunk(rechnung.getBestellung().getKunde().getVorname() + " ", normalFont));
        kundenInfo.add(new Chunk(rechnung.getBestellung().getKunde().getName() + "\n", normalFont));
        kundenInfo.add(new Chunk(rechnung.getBestellung().getKunde().getStrasse() + " ", normalFont));
        kundenInfo.add(new Chunk(rechnung.getBestellung().getKunde().getHausnummer() + "\n", normalFont));
        kundenInfo.add(new Chunk(rechnung.getBestellung().getKunde().getPostleitzahl() + " ", normalFont));
        kundenInfo.add(new Chunk(rechnung.getBestellung().getKunde().getOrt() + "\n", normalFont));

        document.add(kundenInfo);
    }

    private void addRechnungsDetails(Document document, Rechnung rechnung) throws DocumentException {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(createCell("Rechnungsnummer:", boldFont));
        table.addCell(createCell(rechnung.getRechnungsnummer(), normalFont));

        table.addCell(createCell("Rechnungsdatum:", boldFont));
        table.addCell(createCell(rechnung.getErstellungsdatum().format(DATE_FORMATTER), normalFont));

        table.addCell(createCell("Bestellnummer:", boldFont));
        table.addCell(createCell(String.valueOf(rechnung.getBestellung().getId()), normalFont));

        document.add(table);
    }

    private void addPositionenTabelle(Document document, Rechnung rechnung) throws DocumentException {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1, 2, 2 });

        // Header
        table.addCell(createHeaderCell("Produkt", boldFont));
        table.addCell(createHeaderCell("Menge", boldFont));
        table.addCell(createHeaderCell("Einzelpreis", boldFont));
        table.addCell(createHeaderCell("Gesamtpreis", boldFont));

        // Items
        for (BestellPosition position : rechnung.getBestellung().getPositionen()) {
            table.addCell(createCell(position.getProdukt().getName(), normalFont));
            table.addCell(createCell(String.valueOf(position.getMenge()), normalFont));
            table.addCell(createCell(formatPrice(position.getProdukt().getPreis()), normalFont));

            BigDecimal gesamtpreis = position.getProdukt().getPreis()
                    .multiply(BigDecimal.valueOf(position.getMenge()));
            table.addCell(createCell(formatPrice(gesamtpreis), normalFont));
        }

        document.add(table);
    }

    private void addGesamtsumme(Document document, Rechnung rechnung) throws DocumentException {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        Paragraph total = new Paragraph();
        total.setAlignment(Element.ALIGN_RIGHT);
        total.add(new Chunk("Gesamtbetrag: " + formatPrice(rechnung.getGesamtbetrag()), boldFont));
        document.add(total);
    }

    private void addFooter(Document document) throws DocumentException {
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 8);
        Paragraph footer = new Paragraph(
                "Zahlbar innerhalb von 14 Tagen ohne Abzug.\n" +
                        "Bankverbindung: IBAN DE12 3456 7890 1234 5678 90 | BIC: ABCDEFGH",
                smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        return cell;
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%.2f €", price);
    }
}
