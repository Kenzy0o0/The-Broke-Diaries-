package com.budgetapp.UI;

import java.io.IOException;

import com.budgetapp.controller.AuthManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * <p>
 * RegisterController class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
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
     * Initializes the RegisterController and links the
     * {@link com.budgetapp.controller.AuthManager} instance.
     */
    public RegisterController() {
        authManager = AuthManager.getInstance();
    }

    /**
     * <p>handleRegisterAction.</p>
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
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
            UIManager.switchScene(e, "/fxml/login.fxml");
        } else {
            label.setText("Email already exists!");
            label.setStyle("-fx-text-fill:red;");
        }

    }

    /**
     * Redirects the user to the login screen without processing any data.
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleGoTologin(ActionEvent e) throws IOException {
        label.setText("Redirecting to Login form....");
        UIManager.switchScene(e, "/fxml/login.fxml");

    }

}
