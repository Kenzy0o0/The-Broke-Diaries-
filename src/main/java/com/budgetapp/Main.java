package com.budgetapp;

import com.budgetapp.infrastructure.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <p>
 * Main class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
public class Main extends Application {

    /**
     * {@inheritDoc}
     *
     * The main entry point for all JavaFX applications. It handles the initial
     * setup of the database and the first UI scene.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.getInstance().initializeDatabase();

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/login.fxml")
        );
        primaryStage.setTitle("The Broke Diaries");
        primaryStage.setScene(new Scene(root, 900, 650));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * <p>
     * main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        launch(args);
    }
}
