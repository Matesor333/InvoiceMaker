package demo.prorotypeinvocemaker;

import javafx.beans.property.SimpleStringProperty;
import java.io.File;
import java.time.LocalDate;

public class InvoiceRecord {
    private final SimpleStringProperty invoiceId;
    private final SimpleStringProperty type;
    private final SimpleStringProperty customer;
    private final SimpleStringProperty date;
    private final SimpleStringProperty total;
    private final File file;
    private final LocalDate parsedDate;

    public InvoiceRecord(String invoiceId, String type, String customer,
                         String date, String total, File file, LocalDate parsedDate) {
        this.invoiceId = new SimpleStringProperty(invoiceId);
        this.type = new SimpleStringProperty(type);
        this.customer = new SimpleStringProperty(customer);
        this.date = new SimpleStringProperty(date);
        this.total = new SimpleStringProperty(total);
        this.file = file;
        this.parsedDate = parsedDate;
    }

    // Getters
    public String getInvoiceId() { return invoiceId.get(); }
    public String getType() { return type.get(); }
    public String getCustomer() { return customer.get(); }
    public String getDate() { return date.get(); }
    public String getTotal() { return total.get(); }
    public File getFile() { return file; }
    public LocalDate getParsedDate() { return parsedDate; }

    // Property accessors for TableView
    public SimpleStringProperty invoiceIdProperty() { return invoiceId; }
    public SimpleStringProperty typeProperty() { return type; }
    public SimpleStringProperty customerProperty() { return customer; }
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty totalProperty() { return total; }
}
