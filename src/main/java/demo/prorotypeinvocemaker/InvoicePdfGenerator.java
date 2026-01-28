package demo.prorotypeinvocemaker;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            String outputPath) throws IOException {

        // Create filename from invoice ID (remove special characters)
        String filename = "invoice_" + invoiceId.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        String pdfPath = outputPath + "/" + filename;

        // Step 1: Create PdfWriter to write to file
        PdfWriter writer = new PdfWriter(pdfPath);

        // Step 2: Create PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Step 3: Create Document (high-level layout)
        Document document = new Document(pdfDoc);

        // --- ADD CONTENT TO PDF ---

        // Title
        document.add(new Paragraph("INVOICE")
                .setFontSize(24).setBold().setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n"));

        // Invoice Details
        document.add(new Paragraph("Invoice ID: " + invoiceId).setBold());
        document.add(new Paragraph("Invoice Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("Payment Due: " + dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        document.add(new Paragraph("\n"));

        // Customer Details
        document.add(new Paragraph("BILL TO:").setBold().setFontSize(12));
        document.add(new Paragraph(customerName));
        document.add(new Paragraph(address));
        document.add(new Paragraph(city + ", " + postcode + ", " + country));

        if ("Company".equals(customerType)) {
            if (companyId != null && !companyId.isEmpty()) {
                document.add(new Paragraph("Company ID: " + companyId));
            }
            if (vatNumber != null && !vatNumber.isEmpty()) {
                document.add(new Paragraph("VAT Number: " + vatNumber));
            }
        }

        document.add(new Paragraph("\n"));

        // Items Table (2 columns: Service and Amount)
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        // Header Row
        Cell header1 = new Cell().add(new Paragraph("Service Description").setBold());
        Cell header2 = new Cell().add(new Paragraph("Amount").setBold());
        header2.setTextAlignment(TextAlignment.RIGHT);
        table.addCell(header1);
        table.addCell(header2);

        // Data Rows
        double total = 0;
        String currencySymbol = getCurrencySymbol(currency);

        for (InvoiceItem item : items) {
            table.addCell(item.getDescription());

            Cell amountCell = new Cell().add(
                    new Paragraph(String.format("%s %.2f", currencySymbol, item.getAmount()))
            );
            amountCell.setTextAlignment(TextAlignment.RIGHT);
            table.addCell(amountCell);

            total += item.getAmount();
        }

        // Total Row
        Cell totalLabel = new Cell().add(new Paragraph("TOTAL").setBold());
        Cell totalAmount = new Cell().add(
                new Paragraph(String.format("%s %.2f", currencySymbol, total)).setBold()
        );
        totalAmount.setTextAlignment(TextAlignment.RIGHT);

        table.addCell(totalLabel);
        table.addCell(totalAmount);

        document.add(table);

        // Footer
        document.add(new Paragraph("\n\nThank you for your business!")
                .setTextAlignment(TextAlignment.CENTER));

        // Step 4: Close the document (IMPORTANT!)
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
