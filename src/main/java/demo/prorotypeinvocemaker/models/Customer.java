package demo.prorotypeinvocemaker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

// 1. This annotation tells Jackson to ignore any extra fields from Supabase
// (like "created_at") that aren't in this Java class, preventing crashes.
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;


    @JsonProperty("id")
    private String internalId;


    @JsonProperty("company_reg_number")
    private String id;


    private String name;
    private String address;
    private String city;
    private String postcode;
    private String country;

    @JsonProperty("vat")
    private String vat;

    private String type;
    private String note;


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



    public String getInternalId() { return internalId; }
    public void setInternalId(String internalId) { this.internalId = internalId; }

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
        return name; // Important for ComboBox display in UI
    }
}
