package demo.prorotypeinvocemaker.helperClass;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import demo.prorotypeinvocemaker.models.Customer;
public class CustomerManager {
    private static final String FILE_NAME = "customers.dat";
    private List<Customer> customers;

    public CustomerManager() {
        customers = loadCustomers();
    }

    public void addOrUpdateCustomer(Customer newCustomer) {
        customers = loadCustomers();
        // Check if customer exists (by name and type) and update
        customers.removeIf(c -> c.getName().equalsIgnoreCase(newCustomer.getName())
                && c.getType().equals(newCustomer.getType()));

        customers.add(newCustomer);
        saveCustomers();
    }

    public List<Customer> getAllCustomers() {
        customers = loadCustomers();
        return new ArrayList<>(customers);
    }

    public void deleteCustomer(Customer customer) {
        customers = loadCustomers();
        customers.removeIf(c -> c.getName().equalsIgnoreCase(customer.getName())
                && c.getType().equals(customer.getType()));
        saveCustomers();
    }

    public List<Customer> getCustomersByType(String type) {
        customers = loadCustomers();
        return customers.stream()
                .filter(c -> c.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    private void saveCustomers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Customer> loadCustomers() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (List<Customer>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}