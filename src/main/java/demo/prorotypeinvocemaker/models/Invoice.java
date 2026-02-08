package demo.prorotypeinvocemaker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Invoice {
    
    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("issue_date")
    private String issueDate;

    @JsonProperty("total_amount")
    private double totalAmount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("pdf_url")
    private String pdfUrl;

    @JsonProperty("due_date")
    private String dueDate;

    public Invoice() {}

    public Invoice(String invoiceNumber, String customerId, String issueDate, double totalAmount, String currency, String pdfUrl, String dueDate) {
        this.invoiceNumber = invoiceNumber;
        this.customerId = customerId;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.pdfUrl = pdfUrl;
        this.dueDate = dueDate;
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}
