package com.budgetapp.UI;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.controller.BudgetManager;
import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Category;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BudgetScreen {

    // ── FXML Links ──
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField limitField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label messageLabel;
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> categoryCol;
    @FXML private TableColumn<Budget, Double> limitCol;
    @FXML private TableColumn<Budget, Double> spentCol;
    @FXML private TableColumn<Budget, Double> remainingCol;
    @FXML private TableColumn<Budget, Void> deleteCol;    

    private List<Category> categoryList;
    private BudgetManager budgetManager = new BudgetManager();

    @FXML
    public void initialize() {
        loadCategories();
        setupTable();
        loadBudgets();
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

    private void setupTable() {

        categoryCol.setCellValueFactory(data -> {
            int cId = data.getValue().getCategoryId();
            String name = "Unknown";
            if (categoryList != null) {
                for (Category c : categoryList) {
                    if (c.getCategoryId() == cId) {
                        name = c.getName();
                        break;
                    }
                }
            }
            return new SimpleStringProperty(name);
        });

        // Limit
        limitCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getLimit()).asObject()
        );

        // Spent
        spentCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getCurrentSpent()).asObject()
        );

        // Remaining
        remainingCol.setCellValueFactory(data -> {
            double remaining = data.getValue().getLimit()
                             - data.getValue().getCurrentSpent();
            return new SimpleDoubleProperty(remaining).asObject();
        });

        deleteCol.setCellFactory(col -> new TableCell<Budget, Void>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle(
                    "-fx-background-color: #f44336;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11;" +
                    "-fx-cursor: hand;"
                );
                deleteBtn.setOnAction(e -> {
                    Budget selected = getTableView().getItems().get(getIndex());
                    budgetManager.deleteBudget(selected.getBudgetId());
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Budget deleted.");
                    loadBudgets(); // refresh table
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void loadBudgets() {
        int userId = AuthManager.getInstance().getCurrentUser().getId();
        List<Budget> budgets = budgetManager.getBudgets(userId);
        if (budgets == null) return;
        budgetTable.setItems(FXCollections.observableArrayList(budgets));
    }

    @FXML
    private void handleCreate() {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText("");

        String limitText  = limitField.getText().trim();
        int selectedIndex = categoryComboBox.getSelectionModel().getSelectedIndex();

        // Validate
        if (selectedIndex < 0) {
            messageLabel.setText("Please select a category");
            return;
        }
        if (limitText.isEmpty()) {
            messageLabel.setText("Please enter a limit amount");
            return;
        }
        if (startDatePicker.getValue() == null) {
            messageLabel.setText("Please select a start date");
            return;
        }
        if (endDatePicker.getValue() == null) {
            messageLabel.setText("Please select an end date");
            return;
        }

        // Parse limit
        double limit;
        try {
            limit = Double.parseDouble(limitText);
            if (limit <= 0) {
                messageLabel.setText("Limit must be greater than zero");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Limit must be a valid number");
            return;
        }

        // Convert dates
        Date startDate = Date.from(
            startDatePicker.getValue()
                           .atStartOfDay(ZoneId.systemDefault())
                           .toInstant()
        );
        Date endDate = Date.from(
            endDatePicker.getValue()
                         .atStartOfDay(ZoneId.systemDefault())
                         .toInstant()
        );

        // Validate end date is after start date
        if (endDate.before(startDate)) {
            messageLabel.setText("End date must be after start date");
            return;
        }

        int userId = AuthManager.getInstance().getCurrentUser().getId();
        Category selectedCategory = categoryList.get(selectedIndex);

        budgetManager.createBudget(
            userId,
            limit,
            selectedCategory.getCategoryId(),
            startDate,
            endDate
        );

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Budget created successfully!");
        clearForm();
        loadBudgets();
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/dashboard.fxml")
            );
            Stage stage = (Stage) limitField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 650));
        } catch (Exception e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    private void clearForm() {
        limitField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}