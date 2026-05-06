package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * <p>
 * LoginController class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
public class LoginController {

    /**
     * Input field for the user's email address.
     */
    @FXML
    private TextField emailField;

    /**
     * Input field for the user's password (masks characters for security).
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Feedback label used to display error messages or status updates to the
     * user.
     */
    @FXML
    private Label label;

    /**
     * Service responsible for verifying credentials and managing the session.
     */
    private AuthManager authManager;

    /**
     * Initializes the LoginController and connects it to the singleton
     * AuthManager.
     */
    public LoginController() {
        authManager = AuthManager.getInstance();
    }

    /**
     * Validates input fields and attempts to authenticate the user. If
     * successful, redirects to the Dashboard; otherwise, displays an error.
     *
     * @param e The click event from the "Login" button.
     * @throws java.io.IOException If the dashboard view fails to load.
     */
    @FXML
    public void handleLoginAction(ActionEvent e) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            label.setText("Please Enter Both Email and Password");
            label.setStyle("-fx-text-fill:red;");
            return;
        }
        boolean isLoged = authManager.login(email, password);
        if (isLoged) {
            label.setText("Login Successful! Redirecting.... ");
            label.setStyle("-fx-text-fill:green;");
            //open Dashbord
            UIManager.switchScene(e, "/fxml/dashboard.fxml");
        } else {
            label.setText("Invalid Email or Password ");
            label.setStyle("-fx-text-fill:red;");
        }

    }

    /**
     * Redirects the user to the registration screen.
     *
     * @param e The click event from the "Register" or "Sign Up" link/button.
     * @throws java.io.IOException If the registration view fails to load.
     */
    @FXML
    public void handleGoToRegister(ActionEvent e) throws IOException {
        label.setText("Redirecting to Registration form....");
        //open Register
        UIManager.switchScene(e, "/fxml/register.fxml");
    }

}
