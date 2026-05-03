package com.budgetapp;

import com.budgetapp.infrastructure.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.getInstance().initializeDatabase();

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/login.fxml")
        );
        primaryStage.setTitle("The Broke Diaries");
        primaryStage.setScene(new Scene(root, 900, 650));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
