package demo.prorotypeinvocemaker.helperClass;

import demo.prorotypeinvocemaker.managers.SupabaseClient;
import demo.prorotypeinvocemaker.models.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerManager {
    private final SupabaseClient supabaseClient;
    private List<Customer> customers;

    public CustomerManager() {
        this.supabaseClient = new SupabaseClient();
        customers = loadCustomers();
    }

    public void addOrUpdateCustomer(Customer newCustomer) {
        supabaseClient.upsertCustomer(newCustomer);
        customers = loadCustomers();
    }

    public List<Customer> getAllCustomers() {
        customers = loadCustomers();
        return new ArrayList<>(customers);
    }

    public void deleteCustomer(Customer customer) {
        supabaseClient.deleteCustomer(customer.getId());
        customers = loadCustomers();
    }

    public List<Customer> getCustomersByType(String type) {
        customers = loadCustomers();
        return customers.stream()
                .filter(c -> c.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    private List<Customer> loadCustomers() {
        return supabaseClient.getAllCustomers();
    }
}