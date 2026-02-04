package demo.prorotypeinvocemaker.Controllers;

import demo.prorotypeinvocemaker.helperClass.CustomerManager;
import demo.prorotypeinvocemaker.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ClientManagementController {

    @FXML private TableView<Customer> clientTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> typeColumn;
    @FXML private TableColumn<Customer, String> cityColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> idColumn;

    private CustomerManager customerManager;
    private ObservableList<Customer> clientList;

    @FXML
    public void initialize() {
        customerManager = new CustomerManager();
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        clientTable.setRowFactory(tv -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Customer rowData = row.getItem();
                    openClientDetail(rowData);
                }
            });
            return row;
        });

        loadClients();
        demo.prorotypeinvocemaker.managers.RefreshManager.addRefreshTask(this::loadClients);
    }

    private void openClientDetail(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/demo/prorotypeinvocemaker/client-detail.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            scene.getStylesheets().add(getClass().getResource("/demo/prorotypeinvocemaker/styles.css").toExternalForm());

            ClientDetailController controller = loader.getController();
            controller.setCustomer(customer);

            Stage stage = new Stage();
            stage.setTitle("Client Detail - " + customer.getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClients() {
        clientList = FXCollections.observableArrayList(customerManager.getAllCustomers());
        clientTable.setItems(clientList);
    }

    @FXML
    private void handleAddClient() {
        showClientDialog(null);
    }

    @FXML
    private void handleEditClient() {
        Customer selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            showClientDialog(selectedClient);
        } else {
            showAlert("No Selection", "Please select a client to edit.");
        }
    }

    @FXML
    private void handleDeleteClient() {
        Customer selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Client");
            alert.setHeaderText("Are you sure you want to delete this client?");
            alert.setContentText(selectedClient.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                customerManager.deleteCustomer(selectedClient);
                loadClients();
            }
        } else {
            showAlert("No Selection", "Please select a client to delete.");
        }
    }

    private void showClientDialog(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(customer == null ? "Add New Client" : "Edit Client");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField postcodeField = new TextField();
        TextField countryField = new TextField();
        TextField idField = new TextField();
        TextField vatField = new TextField();
        TextArea noteArea = new TextArea();
        noteArea.setPrefRowCount(3);
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList("Company", "Person"));

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("City:"), 0, 3);
        grid.add(cityField, 1, 3);
        grid.add(new Label("Postcode:"), 0, 4);
        grid.add(postcodeField, 1, 4);
        grid.add(new Label("Country:"), 0, 5);
        grid.add(countryField, 1, 5);
        grid.add(new Label("ID/IČO:"), 0, 6);
        grid.add(idField, 1, 6);
        grid.add(new Label("VAT/DIČ:"), 0, 7);
        grid.add(vatField, 1, 7);
        grid.add(new Label("Note:"), 0, 8);
        grid.add(noteArea, 1, 8);

        if (customer != null) {
            typeComboBox.setValue(customer.getType());
            nameField.setText(customer.getName());
            addressField.setText(customer.getAddress());
            cityField.setText(customer.getCity());
            postcodeField.setText(customer.getPostcode());
            countryField.setText(customer.getCountry());
            idField.setText(customer.getId());
            vatField.setText(customer.getVat());
            noteArea.setText(customer.getNote());
            // When editing, name and type might be used as keys in CustomerManager.addOrUpdateCustomer
        } else {
            typeComboBox.setValue("Company");
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Customer(
                        nameField.getText(),
                        addressField.getText(),
                        cityField.getText(),
                        postcodeField.getText(),
                        countryField.getText(),
                        idField.getText(),
                        vatField.getText(),
                        typeComboBox.getValue(),
                        noteArea.getText()
                );
            }
            return null;
        });

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/demo/prorotypeinvocemaker/styles.css").toExternalForm());

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(newCustomer -> {
            customerManager.addOrUpdateCustomer(newCustomer);
            loadClients();
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
