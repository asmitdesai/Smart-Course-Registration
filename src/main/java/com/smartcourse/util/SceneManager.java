package com.smartcourse.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.smartcourse.App;
import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }

    public static <T> T loadController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
