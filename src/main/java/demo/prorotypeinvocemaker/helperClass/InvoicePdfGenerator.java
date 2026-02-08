package demo.prorotypeinvocemaker.helperClass;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import demo.prorotypeinvocemaker.models.InvoiceItem;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class InvoicePdfGenerator {

    public static void generateInvoice(
            String invoiceId,
            String customerName,
            String address,
            String city,
            String postcode,
            String country,
            String companyId,
            String vatNumber,
            String customerType,
            List<InvoiceItem> items,
            LocalDate dueDate,
            String currency,
            String outputPath,
            Locale locale,
            java.util.Properties companyDetails) throws IOException { // <--- Added companyDetails parameter

        // Load translations
        ResourceBundle messages = ResourceBundle.getBundle("demo.prorotypeinvocemaker.messages", locale);

        String filename = "invoice_" + invoiceId.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        String pdfPath = outputPath + "/" + filename;

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont font;
        try {
            // Try to use Arial from Windows Fonts as it supports Slovak characters and Cp1250 encoding
            String fontPath = "C:\\Windows\\Fonts\\arial.ttf";
            if (new File(fontPath).exists()) {
                font = PdfFontFactory.createFont(fontPath, "Cp1250", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            } else {
                // Fallback to Helvetica if Arial is not found (though it might have issues with some characters)
                font = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA, "Cp1250", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            }
        } catch (Exception e) {
            font = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA, "Cp1250", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }
        document.setFont(font);

        // Title
        document.add(new Paragraph(messages.getString("invoice.title"))
                .setFontSize(24).setBold().setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n"));

        // Main content table: Company Details (Left) and Invoice Details (Right)
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Company Details (Ours)
        Cell companyCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
        companyCell.add(new Paragraph(messages.getString("invoice.company.details")).setBold().setFontSize(12));
        companyCell.add(new Paragraph(companyDetails.getProperty("companyName", "")));
        companyCell.add(new Paragraph(companyDetails.getProperty("addressLine1", "")));
        String address2 = companyDetails.getProperty("addressLine2", "");
        if (address2 != null && !address2.isEmpty()) {
            companyCell.add(new Paragraph(address2));
        }
        companyCell.add(new Paragraph(companyDetails.getProperty("city", "") + ", " + companyDetails.getProperty("postcode", "")));
        companyCell.add(new Paragraph(messages.getString("invoice.companyid") + " " + companyDetails.getProperty("companyId", "")));
        companyCell.add(new Paragraph(messages.getString("invoice.vat") + " " + companyDetails.getProperty("taxNumber", "")));
        headerTable.addCell(companyCell);

        // Invoice Details
        Cell invoiceDetailsCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
        invoiceDetailsCell.setTextAlignment(TextAlignment.RIGHT);
        invoiceDetailsCell.add(new Paragraph("Invoice #: " + invoiceId).setBold());
        invoiceDetailsCell.add(new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        invoiceDetailsCell.add(new Paragraph(messages.getString("invoice.due") + " " + dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        headerTable.addCell(invoiceDetailsCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));

        // Customer Details
        document.add(new Paragraph(messages.getString("invoice.billto")).setBold().setFontSize(12));
        document.add(new Paragraph(customerName));
        document.add(new Paragraph(address));
        document.add(new Paragraph(city + ", " + postcode + ", " + country));

        if ("Company".equals(customerType)) {
            if (companyId != null && !companyId.isEmpty()) {
                document.add(new Paragraph(messages.getString("invoice.companyid") + " " + companyId));
            }
            if (vatNumber != null && !vatNumber.isEmpty()) {
                document.add(new Paragraph(messages.getString("invoice.vat") + " " + vatNumber));
            }
        }

        document.add(new Paragraph("\n"));

        // Items Table
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        Cell header1 = new Cell().add(new Paragraph(messages.getString("invoice.service")).setBold());
        Cell header2 = new Cell().add(new Paragraph(messages.getString("invoice.amount")).setBold());
        header2.setTextAlignment(TextAlignment.RIGHT);
        table.addCell(header1);
        table.addCell(header2);

        double total = 0;
        String currencySymbol = getCurrencySymbol(currency);

        for (InvoiceItem item : items) {
            table.addCell(new Paragraph(item.getDescription())); // Ensure font is applied
            Cell amountCell = new Cell().add(new Paragraph(String.format("%s %.2f", currencySymbol, item.getAmount())));
            amountCell.setTextAlignment(TextAlignment.RIGHT);
            table.addCell(amountCell);
            total += item.getAmount();
        }

        Cell totalLabel = new Cell().add(new Paragraph(messages.getString("invoice.total")).setBold());
        Cell totalAmount = new Cell().add(new Paragraph(String.format("%s %.2f", currencySymbol, total)).setBold());
        totalAmount.setTextAlignment(TextAlignment.RIGHT);

        table.addCell(totalLabel);
        table.addCell(totalAmount);

        document.add(table);

        document.add(new Paragraph("\n"));

        // Bank Details
        document.add(new Paragraph(messages.getString("invoice.bank.title")).setBold().setFontSize(12));
        document.add(new Paragraph(messages.getString("invoice.bank.name") + " " + companyDetails.getProperty("bankName", "")));
        document.add(new Paragraph(messages.getString("invoice.bank.account") + " " + companyDetails.getProperty("accountName", "")));
        document.add(new Paragraph(messages.getString("invoice.bank.iban") + " " + companyDetails.getProperty("iban", "")));
        document.add(new Paragraph(messages.getString("invoice.bank.swift") + " " + companyDetails.getProperty("swift", "")));

        document.add(new Paragraph("\n\n" + messages.getString("invoice.footer"))
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        System.out.println("✓ PDF generated: " + pdfPath);
    }


    private static String getCurrencySymbol(String currency) {

        if (currency == null) return "£";
        if (currency.contains("USD")) return "$";
        if (currency.contains("EUR")) return "€";
        return "£"; // Default GBP
    }
}