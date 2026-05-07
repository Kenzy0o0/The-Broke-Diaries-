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
   static final double WIDTH = 900;
    static final double HEIGHT = 650;

    /**
     * Utility method to transition the application to a different view.
     *
     * @param e The ActionEvent triggered by the button click
     * @param path The relative path to the target .fxml file
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(
                UIManager.class.getResource(fxmlPath)
            );
 
            Stage stage = (Stage) ((Node) event.getSource())
                                      .getScene()
                                      .getWindow();
 
            // Build the new scene at the fixed application size
            Scene scene = new Scene(root, WIDTH, HEIGHT);
 
            stage.setScene(scene);          // set exactly ONCE
            stage.setResizable(false);
            stage.show();
 
        } catch (IOException e) {
            System.err.println("UIManager: failed to load FXML → " + fxmlPath);
            e.printStackTrace();
        }
    }
}
