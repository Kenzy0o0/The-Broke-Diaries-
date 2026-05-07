package com.budgetapp.UI;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <p>
 * UIManager class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
public class UIManager {

    // Constants for your consistent dimensions
    private static final double WIDTH = 900;
    private static final double HEIGHT = 650;

    /**
     * Utility method to transition the application to a different view.
     *
     * @param e The ActionEvent triggered by the button click
     * @param path The relative path to the target .fxml file
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            // load new fxml
            Parent root = FXMLLoader.load(UIManager.class.getResource(fxmlPath));

            // 2. Get the current Stage from the button/event that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean isMaximized = stage.isMaximized();

            Scene scene = new Scene(root, 900, 650);
            stage.setScene(scene);
            stage.setResizable(false);
            // 4. Apply to stage
            if (isMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            }

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
