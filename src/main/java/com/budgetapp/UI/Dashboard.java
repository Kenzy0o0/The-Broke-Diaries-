package com.budgetapp.UI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.controller.TransactionManager;
import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Transaction;
import com.budgetapp.model.User;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class Dashboard {

    // ! temp solution
    // the @FXML annotation is used to link the UI elements in the FXML file to the controller
    @FXML
    private Label totalBalanceLabel;
    @FXML
    private Label totalIncomeLabel;
    @FXML
    private Label totalExpenseLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label budgetWarningLabel;
    @FXML
    private ListView<String> recentTransactionsList;

    private AuthManager authManager = AuthManager.getInstance();

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
        double currentBalance = tm.getCurrentBalance(currentUser);
        double totalIncomeThisMonth = tm.getTotalIncomeThisMonth(currentUser);
        double totalExpenseThisMonth = tm.getTotalExpenseThisMonth(currentUser);

        totalBalanceLabel.setText(String.valueOf(currentBalance));
        totalIncomeLabel.setText("Total Income This Month: " + totalIncomeThisMonth);
        totalExpenseLabel.setText("Total Expense This Month: " + totalExpenseThisMonth);
    }

    public void loadRecentTransactions(User currentUser) {
        List<Transaction> all = db.fetchTransactions(currentUser.getId());
        List<String> display = new ArrayList<>();

        if (all == null || all.isEmpty()) {
            display.add("No transactions yet.");
        } else {
            // show last 5 only
            int count = Math.min(5, all.size());
            for (int i = 0; i < count; i++) {
                Transaction t = all.get(i);
                String sign = t.getType().equals("income") ? "+" : "-";
                display.add(sign + t.getAmount() + " | "
                        + t.getDescription() + " | " + t.getDate());
            }
        }

        recentTransactionsList.setItems(FXCollections.observableArrayList(display));
    }

    private void switchScene(ActionEvent e, String path) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void handleGoToTransactions(ActionEvent e) throws IOException {
        switchScene(e, "/fxml/transaction.fxml");
    }

    @FXML
    public void handleGoToBudget(ActionEvent e) throws IOException {
        switchScene(e, "/fxml/budget.fxml");
    }

    @FXML
    public void handleGoToReports(ActionEvent e) throws IOException {
        switchScene(e, "/fxml/report.fxml");
    }

    @FXML
    public void handleGoToProfile(ActionEvent e) throws IOException {
        switchScene(e, "/fxml/profile.fxml");
    }

    @FXML
    public void handleSignOut(ActionEvent e) throws IOException {
        authManager.logout();
        switchScene(e, "/fxml/login.fxml");
    }
}