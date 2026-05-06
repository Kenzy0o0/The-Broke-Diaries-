package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    /**
     * Input field for the user's full name.
     */
    @FXML
    private TextField nameField;

    /**
     * Input field for the user's login email (must be unique).
     */
    @FXML
    private TextField emailField;

    /**
     * Input field for the account password.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Input field for the user's primary currency (e.g., USD, EUR).
     */
    @FXML
    private TextField currencyField;

    /**
     * Status label used to display validation errors or success messages.
     */
    @FXML
    private Label label;

    private AuthManager authManager;

    /**
     * Initializes the RegisterController and links the {@link AuthManager}
     * instance.
     */
    public RegisterController() {
        authManager = AuthManager.getInstance();
    }

    /**
     * General utility to swap the current stage's scene.
     *
     * @param e the action event triggering the switch
     * @param s the FXML file path
     * @throws IOException if the FXML resource is missing
     */
    private void switchScene(ActionEvent e, String s) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(s));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * General utility to swap the current stage's scene.
     *
     * @param e the action event triggering the switch
     * @param s the FXML file path
     * @throws IOException if the FXML resource is missing
     */
    @FXML
    public void handleRegisterAction(ActionEvent e) throws IOException {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String currency = currencyField.getText();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || currency.isEmpty()) {
            label.setText("Please Fill All Fields!!");
            label.setStyle("-fx-text-fill:red;");
            return;
        }
        boolean registered = authManager.register(name, email, password, currency);
        if (registered) {
            label.setText("Account created Successfully! please login.");
            label.setStyle("-fx-text-fill:green;");
            //open login
            switchScene(e, "/fxml/login.fxml");
        } else {
            label.setText("Email already exists!");
            label.setStyle("-fx-text-fill:red;");
        }

    }

    /**
     * Redirects the user to the login screen without processing any data.
     */
    @FXML
    public void handleGoTologin(ActionEvent e) throws IOException {
        label.setText("Redirecting to Login form....");
        switchScene(e, "/fxml/login.fxml");

    }

}
