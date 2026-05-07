package com.budgetapp.UI;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.controller.TransactionManager;
import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * <p>
 * TransactionScreen class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
public class TransactionScreen {

    /**
     * Toggle to determine if the transaction is an Income or Expense.
     */
    @FXML
    private RadioButton incomeRadio;

    /**
     * Input for the transaction's monetary value.
     */
    @FXML
    private TextField amountField;

    /**
     * Dropdown populated with available spending/earning categories.
     */
    @FXML
    private ComboBox<String> categoryComboBox;

    /**
     * Graphical calendar for selecting the transaction date.
     */
    @FXML
    private DatePicker datePicker;

    /**
     * Context-sensitive label that changes between "Source" and "Payment
     * Method".
     */
    @FXML
    private Label extraLabel;

    /**
     * Status label for providing real-time validation feedback.
     */
    @FXML
    private Label messageLabel;

    @FXML
    private TextField descriptionField;
    @FXML
    private TextField extraField;

    private List<Category> categoryList;

    /**
     * Called automatically upon screen load. Populates the category dropdown
     * with data from the database.
     */
    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        categoryList = DatabaseManager.getInstance().fetchCategories(0);
        if (categoryList == null) {
            return;
        }

        ObservableList<String> names = FXCollections.observableArrayList();
        for (Category c : categoryList) {
            names.add(c.getName());
        }
        categoryComboBox.setItems(names);
    }

    /**
     * Responds to the user toggling between Income and Expense. Updates field
     * labels and prompt texts to reflect the relevant context (e.g., changing
     * "Payment Method" to "Source" for income).
     */
    @FXML
    private void handleTypeChange() {
        if (incomeRadio.isSelected()) {
            extraLabel.setText("Source");
            extraField.setPromptText("Enter source");
        } else {
            extraLabel.setText("Payment Method");
            extraField.setPromptText("Enter payment method");
        }
    }

    /**
     * Primary action handler for the "Save" button. 1. Validates that no fields
     * are empty. 2. Safely parses numerical input and handles formatting
     * errors. 3. Converts LocalDate to java.util.Date. 4. Delegates the
     * business logic and persistence to the TransactionManager.
     */
    @FXML
    private void handleSave() {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText("");

        String type = incomeRadio.isSelected() ? "income" : "expense";
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String extra = extraField.getText().trim();
        int selectedIndex = categoryComboBox.getSelectionModel().getSelectedIndex();

        // validate
        if (amountText.isEmpty()) {
            messageLabel.setText("Please enter an amount");
            return;
        }
        if (selectedIndex < 0) {
            messageLabel.setText("Please select a category");
            return;
        }
        if (datePicker.getValue() == null) {
            messageLabel.setText("Please select a date");
            return;
        }
        if (description.isEmpty()) {
            messageLabel.setText("Please enter a description");
            return;
        }
        if (extra.isEmpty()) {
            messageLabel.setText(type.equals("income")
                    ? "Please enter a source"
                    : "Please enter a payment method");
            return;
        }

        // parse amount
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                messageLabel.setText("Amount must be greater than zero");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Amount must be a valid number");
            return;
        }

        // convert date
        Date date = Date.from(
                datePicker.getValue()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        Category selectedCategory = categoryList.get(selectedIndex);

        int userId = AuthManager.getInstance().getCurrentUser().getId();

        TransactionManager tm = new TransactionManager();
        boolean success = tm.addTransaction(userId, type, amount, selectedCategory, date, description, extra);
        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Transaction saved successfully!");
            clearForm();
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Failed to save transaction. Please check your input (e.g., date, amount) and try again.");
        }
    }

    /**
     * Returns the user to the main Dashboard view.
     */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/dashboard.fxml")
            );
            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 650));
        } catch (Exception e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    /**
     * Returns the UI to a neutral state after a successful save. Ensures the
     * form is ready for the next entry without manual deletion.
     */
    private void clearForm() {
        amountField.clear();
        descriptionField.clear();
        extraField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        incomeRadio.setSelected(true);
        extraLabel.setText("Source");
        extraField.setPromptText("Enter source");
    }
}
