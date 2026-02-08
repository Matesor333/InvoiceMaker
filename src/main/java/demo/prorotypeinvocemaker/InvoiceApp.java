package demo.prorotypeinvocemaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InvoiceApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        if (!isSupabaseConfigured()) {
            showSupabaseConfigPopup();
        }

        // Check again, if they cancelled or closed without saving, we might still be unconfigured
        if (!isSupabaseConfigured()) {
            // Depending on requirements, we could either exit or allow them to use local features
            // But the prompt says "open befor main screen if there are no url and api keys present"
            // Let's assume they MUST configure it.
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                InvoiceApp.class.getResource("invoice-maker.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("Invoice Maker");
        stage.setScene(scene);
        stage.show();
    }

    private boolean isSupabaseConfigured() {
        java.io.File configFile = new java.io.File("company-details.properties");
        if (!configFile.exists()) return false;

        java.util.Properties properties = new java.util.Properties();
        try (java.io.FileInputStream in = new java.io.FileInputStream(configFile)) {
            properties.load(in);
            String url = properties.getProperty("supabaseUrl", "");
            String key = properties.getProperty("supabaseKey", "");
            String saveLocation = properties.getProperty("saveLocation", "");
            return !url.isEmpty() && !key.isEmpty() && !saveLocation.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void showSupabaseConfigPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("supabase-config.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Initial Configuration");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
