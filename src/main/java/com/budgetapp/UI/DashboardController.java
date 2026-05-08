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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Popup;

/**
 * <p>
 * Dashboard class.</p>
 *
 * @author WeDon'tHave
 * @version $Id: $Id
 */
public class DashboardController {

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
    @FXML
    private Button notificationButton;
    @FXML
    private Popup notificationPopup;

    /**
     * Manager for session handling and user authentication.
     */
    private AuthManager authManager = AuthManager.getInstance();

    /**
     * Direct database access for fetching raw records.
     */
    private DatabaseManager db = DatabaseManager.getInstance();

    /**
     * High-level business logic for calculating totals and balances.
     */
    private TransactionManager tm = TransactionManager.getInstance();

    // private TransactionManager tm = 
    // the function runs automatically when the screen loads
    /**
     * Automatically invoked by JavaFX after the FXML file is loaded. Sets up
     * the initial state of the dashboard for the current user.
     */
    @FXML
    public void initialize() {
        User currentUser = authManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        //we would load everything in seperate functions

        loadSummary(currentUser);
        loadRecentTransactions(currentUser);
        welcomeLabel.setText("Welcome back, " + currentUser.getName() + "!");
    }

    /**
     * Calculates and displays the high-level financial overview.
     *
     * @param currentUser the user whose data is being summarized
     */
    public void loadSummary(User currentUser) {
        double currentBalance = tm.getCurrentBalance(currentUser);
        double totalIncomeThisMonth = tm.getTotalIncomeThisMonth(currentUser);
        double totalExpenseThisMonth = tm.getTotalExpenseThisMonth(currentUser);

        totalBalanceLabel.setText(String.valueOf(currentBalance));
        totalIncomeLabel.setText("Total Income This Month: " + totalIncomeThisMonth);
        totalExpenseLabel.setText("Total Expense This Month: " + totalExpenseThisMonth);
    }

    /**
     * Fetches the full history but filters and formats the last 5 transactions
     * for a clean, scannable dashboard feed.
     *
     * @param currentUser the user whose history is being displayed
     */
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

    /**
     * Handles navigation to the transaction management screen.
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleGoToTransactions(ActionEvent e) throws IOException {
        UIManager.switchScene(e, "/fxml/transaction.fxml");
    }

    /**
     * Handles navigation to the budget planning screen.
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleGoToBudget(ActionEvent e) throws IOException {
        UIManager.switchScene(e, "/fxml/budget.fxml");
    }

    /**
     * <p>
     * handleGoToReports.</p>
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleGoToReports(ActionEvent e) throws IOException {
        UIManager.switchScene(e, "/fxml/report.fxml");
    }

    /**
     * <p>
     * handleGoToProfile.</p>
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleGoToProfile(ActionEvent e) throws IOException {
        UIManager.switchScene(e, "/fxml/profile.fxml");
    }

    /**
     * Logs the user out via {@link com.budgetapp.controller.AuthManager} and
     * returns to the login screen.
     *
     * @param e a {@link javafx.event.ActionEvent} object
     * @throws java.io.IOException if any.
     */
    @FXML
    public void handleSignOut(ActionEvent e) throws IOException {
        authManager.logout();
        UIManager.switchScene(e, "/fxml/login.fxml");
    }

    /**
     * Displays a popup with the user's unread notifications.
     */
}
