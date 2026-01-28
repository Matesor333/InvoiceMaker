package demo.prorotypeinvocemaker.helperClass;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

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
            Locale locale) throws IOException { // <--- Added Locale parameter

        // Load translations
        ResourceBundle messages = ResourceBundle.getBundle("demo.prorotypeinvocemaker.messages", locale);

        String filename = "invoice_" + invoiceId.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        String pdfPath = outputPath + "/" + filename;

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA, "Cp1250", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        // Title
        document.add(new Paragraph(messages.getString("invoice.title"))
                .setFontSize(24).setBold().setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n"));

        // Invoice Details
        document.add(new Paragraph("Invoice #: " + invoiceId).setBold());
        document.add(new Paragraph(messages.getString("invoice.date") + " " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        document.add(new Paragraph(messages.getString("invoice.due") + " " + dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

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