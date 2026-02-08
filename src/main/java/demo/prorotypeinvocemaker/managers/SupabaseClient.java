package demo.prorotypeinvocemaker.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import demo.prorotypeinvocemaker.models.Customer;
import demo.prorotypeinvocemaker.models.Invoice;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ArrayList;

public class SupabaseClient {
    private static final String SUPABASE_URL = "";
    private static final String API_KEY = "";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public SupabaseClient() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }



    public List<Customer> getAllCustomers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + "/rest/v1/customers?select=*"))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<Customer>>(){});
            } else {
                System.err.println("Error fetching customers: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void upsertCustomer(Customer customer) {
        try {
            String jsonBody = mapper.writeValueAsString(customer);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + "/rest/v1/customers"))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "resolution=merge-duplicates")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                System.err.println("Error upserting customer: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(String companyId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + "/rest/v1/customers?company_reg_number=eq." + companyId))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                System.err.println("Error deleting customer: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Invoice> getAllInvoices() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + "/rest/v1/invoices?select=*"))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<Invoice>>(){});
            } else {
                System.err.println("Error fetching invoices: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void upsertInvoice(Invoice invoice) {
        try {
            String jsonBody = mapper.writeValueAsString(invoice);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + "/rest/v1/invoices"))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "resolution=merge-duplicates")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                System.err.println("Error upserting invoice: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
