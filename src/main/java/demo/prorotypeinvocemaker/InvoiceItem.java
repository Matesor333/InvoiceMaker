package demo.prorotypeinvocemaker;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class InvoiceItem {
    private final SimpleStringProperty description;
    private final SimpleDoubleProperty amount;

    public InvoiceItem(String description, double amount) {
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getDescription() { return description.get(); }
    public double getAmount() { return amount.get(); }

    // Property accessors for TableView
    public SimpleStringProperty descriptionProperty() { return description; }
    public SimpleDoubleProperty amountProperty() { return amount; }
}
