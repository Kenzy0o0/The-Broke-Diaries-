package com.budgetapp.UI;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.controller.ReportGenerator;
import com.budgetapp.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ReportScreen implements Initializable {

    /**
     * Displays the proportional breakdown of spending across different
     * categories.
     */
    @FXML
    private PieChart expensePieChart;

    /**
     * Compares income vs. expenses side-by-side over a time series.
     */
    @FXML
    private BarChart<String, Number> incomeVsExpenseChart;

    /**
     * The categorical axis (usually months) for the bar chart.
     */
    @FXML
    private CategoryAxis xAxis;

    /**
     * The numerical axis (monetary value) for the bar chart.
     */
    @FXML
    private NumberAxis yAxis;

    /**
     * Dynamic text area for providing personalized financial advice or
     * summaries.
     */
    @FXML
    private Label insightLabel;

    @FXML
    private Label totalIncomeLabel;
    @FXML
    private Label totalExpenseLabel;
    @FXML
    private Label noDataLabel;

    /**
     * Reference to {@link ReportGenerator} for complex data aggregation.
     */
    private ReportGenerator reportGenerator = ReportGenerator.getInstance();
    private AuthManager authManager = AuthManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = authManager.getCurrentUser();
        if (currentUser == null) {
            noDataLabel.setText("No user logged in.");
            noDataLabel.setVisible(true);
            return;
        }

        int userId = currentUser.getId();

        loadTotals(userId);
        loadPieChart(userId);
        loadBarChart(userId);
        loadInsight(userId);
    }

    private void loadTotals(int userId) {
        double income = reportGenerator.getTotalIncome(userId);
        double expense = reportGenerator.getTotalExpense(userId);

        totalIncomeLabel.setText(String.format("Total Income: %.2f", income));
        totalExpenseLabel.setText(String.format("Total Expense: %.2f", expense));
    }

    /**
     * Converts a Map of category IDs and totals into a format the PieChart can
     * render. Includes a safety check to hide the chart if no data exists.
     *
     * @param userId the user ID to filter data
     */
    private void loadPieChart(int userId) {
        Map<Integer, Double> byCategory = reportGenerator.getExpensesByCategory(userId);

        if (byCategory.isEmpty()) {
            noDataLabel.setText("No expense data available.");
            noDataLabel.setVisible(true);
            expensePieChart.setVisible(false);
            return;
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Double> entry : byCategory.entrySet()) {
            // using categoryId as label for now
            // Person 2 can replace with category name when Category lookup is ready
            pieData.add(new PieChart.Data("Category " + entry.getKey(),
                    entry.getValue()));
        }

        expensePieChart.setData(pieData);
        expensePieChart.setTitle("Expenses by Category");
        expensePieChart.setVisible(true);
    }

    /**
     * Populates the dual-series bar chart. Creates separate Series for 'Income'
     * and 'Expenses' to allow for side-by-side comparison.
     *
     * @param userId the user ID to filter data
     */
    private void loadBarChart(int userId) {
        Map<String, double[]> byMonth = reportGenerator.getIncomeVsExpenseByMonth(userId);

        if (byMonth.isEmpty()) {
            incomeVsExpenseChart.setVisible(false);
            return;
        }

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        for (Map.Entry<String, double[]> entry : byMonth.entrySet()) {
            incomeSeries.getData().add(
                    new XYChart.Data<>(entry.getKey(), entry.getValue()[0]));
            expenseSeries.getData().add(
                    new XYChart.Data<>(entry.getKey(), entry.getValue()[1]));
        }

        incomeVsExpenseChart.getData().clear();
        incomeVsExpenseChart.getData().addAll(incomeSeries, expenseSeries);
        incomeVsExpenseChart.setVisible(true);
    }

    /**
     * Fetches analytical data (like top spending categories) and updates the UI
     * insight text to help users understand their habits.
     */
    private void loadInsight(int userId) {
        int topCat = reportGenerator.getTopSpendingCategory(userId);
        if (topCat == -1) {
            insightLabel.setText("No spending data yet.");
        } else {
            insightLabel.setText("Highest spending in Category " + topCat);
        }
    }

    // Navigation 
    private void switchScene(ActionEvent e, String path) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void handleGoToDashboard(ActionEvent e) throws IOException {
        switchScene(e, "/fxml/dashboard.fxml");
    }
}
