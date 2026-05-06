package com.budgetapp.UI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * <p>
 * ProfileController class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
public class ProfileController implements Initializable {

    /**
     * Field for displaying and editing the user's full name.
     */
    @FXML
    private TextField nameField;

    /**
     * Field for the user's preferred currency (e.g., USD, GBP).
     */
    @FXML
    private TextField currencyField;

    /**
     * Non-editable or display field for the account email.
     */
    @FXML
    private TextField emailField;

    /**
     * Placeholder field for password security.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Status label for update confirmations or error messages.
     */
    @FXML
    private Label label;

    /**
     * Reference to the {@link AuthManager} to retrieve and update session data.
     */
    private AuthManager authManager;

    /**
     * <p>
     * Constructor for ProfileController.</p>
     */
    public ProfileController() {
        authManager = AuthManager.getInstance();
    }

    /**
     * {@inheritDoc}
     *
     * Called by the FXMLLoader after the FXML is processed. Triggers the
     * initial data fetch to populate the profile form.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCurrentUserData();
    }

    private void switchScene(ActionEvent e, String s) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(s));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Collects updated info from the text fields and requests an update via the
     * AuthManager. Validates that fields are not empty before proceeding.
     *
     * @param e the click event from the "Update" button
     */
    @FXML
    public void handleUpdateProfile(ActionEvent e) {
        String name = nameField.getText();
        String currency = currencyField.getText();
        if (name.isEmpty() || currency.isEmpty()) {
            label.setText("Please Fill All Fields!!");
            label.setStyle("-fx-text-fill:red;");
            return;
        }
        boolean isUpdated = authManager.updateProfile(name, currency);
        if (isUpdated) {

            label.setText("Profile Updated Successfully!");
            label.setStyle("-fx-text-fill:green;");
        } else {
            label.setText("Error Updating profile");
            label.setStyle("-fx-text-fill:red;");
        }

    }

    /**
     * Redirects the user back to the main dashboard.
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleGoTODashboard(ActionEvent e) throws IOException {
        label.setText("Returning to DashBoard");
        switchScene(e, "/fxml/dashboard.fxml");
        //???
    }

    /**
     * Clears the current session through AuthManager and returns to the login
     * screen.
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleSignOut(ActionEvent e) throws IOException {
        authManager.logout();
        label.setText("Signing out....");
        switchScene(e, "/fxml/login.fxml");
    }

    /**
     * Retrieves the logged-in user's information from
     * {@link com.budgetapp.controller.AuthManager} and populates the text
     * fields.
     */
    @FXML
    public void loadCurrentUserData() {
        User currentUser = authManager.getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            currencyField.setText(currentUser.getCurrency());
            emailField.setText(authManager.getCurrentEmail());
            passwordField.setText("************");
        }
    }

}
