package demo.prorotypeinvocemaker.models;

import java.io.Serializable;

public class Customer implements Serializable {
    private String name;
    private String address;
    private String city;
    private String postcode;
    private String country;
    private String id; // Company ID or Person ID
    private String vat;
    private String type; // "Company" or "Person"
    private String note;

    private static final long serialVersionUID = 1L;

    // Default constructor for JSON
    public Customer() {}

    public Customer(String name, String address, String city, String postcode,
                    String country, String id, String vat, String type) {
        this(name, address, city, postcode, country, id, vat, type, "");
    }

    public Customer(String name, String address, String city, String postcode,
                    String country, String id, String vat, String type, String note) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
        this.id = id;
        this.vat = vat;
        this.type = type;
        this.note = note;
    }

    // Getters and Setters (Standard)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVat() { return vat; }
    public void setVat(String vat) { this.vat = vat; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return name; // Important for ComboBox display
    }
}