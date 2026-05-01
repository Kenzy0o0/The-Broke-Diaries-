package com.budgetapp.UI;

import com.budgetapp.controller.TransactionManager;
import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Dashboard {

    // ! temp solution
    // the @FXML annotation is used to link the UI elements in the FXML file to the controller
    @FXML
    private Label totalBalanceLabel;
    @FXML
    private Label totalIncomeLabel;
    @FXML
    private Label totalExpenseLabel;

    private DatabaseManager db = DatabaseManager.getInstance();
    private TransactionManager tm = TransactionManager.getInstance();
    // private TransactionManager tm = 

    // the function runs automatically when the screen loads
    @FXML
    public void initialize() {
        User currentUser = new User(1, "Cinder", 0.0, "Pound");
        if (currentUser == null) {
            return;
        }
        //we would load everything in seperate functions

        loadSummary(currentUser);
        loadRecentTransactions(currentUser);
    }

    public void loadSummary(User currentUser) {
        int currentBalance = tm.getCurrentBalance(currentUser);
        int totalIncomeThisMonth = tm.getTotalIncomeThisMonth(currentUser);
        int totalExpenseThisMonth = tm.getTotalExpenseThisMonth(currentUser);

        totalBalanceLabel.setText(String.valueOf(currentBalance));
        totalIncomeLabel.setText("Total Income This Month: " + totalIncomeThisMonth);
        totalExpenseLabel.setText("Total Expense This Month: " + totalExpenseThisMonth);
    }

    public void loadRecentTransactions(User currentUser) {

    }
}
