package com.budgetapp.UI;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

public class TransactionScreen {

    @FXML private RadioButton incomeRadio;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;
    @FXML private TextField extraField;
    @FXML private Label extraLabel;
    @FXML private Label messageLabel;

    private List<Category> categoryList;

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        categoryList = DatabaseManager.getInstance().fetchCategories(0);
        if (categoryList == null) return;

        ObservableList<String> names = FXCollections.observableArrayList();
        for (Category c : categoryList) {
            names.add(c.getName());
        }
        categoryComboBox.setItems(names);
    }

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

    @FXML
    private void handleSave() {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText("");

        String type        = incomeRadio.isSelected() ? "income" : "expense";
        String amountText  = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String extra       = extraField.getText().trim();
        int selectedIndex  = categoryComboBox.getSelectionModel().getSelectedIndex();

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

        // // TODO: replace with real session userId
        int userId = 1;

        TransactionManager tm = new TransactionManager();
        tm.addTransaction(userId, type, amount,
                          selectedCategory, date,
                          description, extra);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Transaction saved successfully!");
        clearForm();
    }

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