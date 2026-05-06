package com.budgetapp.controller;

// sooo, i need to make a report generator class, that will generate reports based on the transactions, and the user can choose to export it as a pdf or excel file, or just view it in the app, i think we can use a library like Apache POI for excel and iText for pdf, but we can also just generate a simple text file for now, and then we can add the other formats later, we can also add some charts and graphs to make it more visually appealing, but for now we can just focus on the basic functionality of generating a report based on the transactions, and then we can add more features later on.
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Transaction;

/**
 * Generates financial reports and analytics for the user. Provides data for pie
 * charts (expenses by category) and bar charts (income vs expenses by month).
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public class ReportGenerator {

    /**
     * The single instance of ReportGenerator for the application.
     */
    private static ReportGenerator instance;

    /**
     * Reference to the database manager for retrieving transaction records.
     */
    private DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Private constructor to prevent external instantiation.
     */
    private ReportGenerator() {
    }

    /**
     * <p>
     * Getter for the field <code>instance</code>.</p>
     *
     * @return a {@link com.budgetapp.controller.ReportGenerator} object
     */
    public static synchronized ReportGenerator getInstance() {
        if (instance == null) {
            instance = new ReportGenerator();
        }
        return instance;
    }

    // returns map of categoryId → total spent (expenses only)
    /**
     * Calculates total expenses grouped by category for a specific user. This
     * data is typically used for generating pie chart visualizations.
     *
     * @param userId the unique ID of the user
     * @return a map where the key is the Category ID and the value is the sum
     * of expenses
     */
    public Map<Integer, Double> getExpensesByCategory(int userId) {
        List<Transaction> all = db.fetchTransactions(userId);
        Map<Integer, Double> result = new HashMap<>();

        if (all == null) {
            return result;
        }

        for (Transaction t : all) {
            if (t.getType().equals("expense")) {
                int cat = t.getCategoryId();
                result.put(cat, result.getOrDefault(cat, 0.0) + t.getAmount());
            }
        }
        return result;
    }

    // returns map of "yyyy-MM" → [totalIncome, totalExpense]
    /**
     * Aggregates monthly totals for both income and expenses. The results are
     * sorted chronologically by the "yyyy-MM" key.
     *
     * @param userId the unique ID of the user
     * @return a TreeMap where the key is "yyyy-MM" and the value is an array:
     * [totalIncome, totalExpense]
     */
    public Map<String, double[]> getIncomeVsExpenseByMonth(int userId) {
        List<Transaction> all = db.fetchTransactions(userId);
        Map<String, double[]> result = new TreeMap<>();

        if (all == null) {
            return result;
        }

        for (Transaction t : all) {
            // format date to "yyyy-MM"
            Calendar cal = Calendar.getInstance();
            cal.setTime(t.getDate());
            String month = cal.get(Calendar.YEAR) + "-"
                    + String.format("%02d", cal.get(Calendar.MONTH) + 1);

            result.putIfAbsent(month, new double[]{0.0, 0.0});

            if (t.getType().equals("income")) {
                result.get(month)[0] += t.getAmount();
            } else {
                result.get(month)[1] += t.getAmount();
            }
        }
        return result;
    }

    // returns total income for user
    /**
     * Calculates the grand total of all income transactions for a user.
     *
     * @param userId the unique ID of the user
     * @return total income amount
     */
    public double getTotalIncome(int userId) {
        List<Transaction> all = db.fetchTransactions(userId);
        if (all == null) {
            return 0;
        }
        return all.stream()
                .filter(t -> t.getType().equals("income"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // returns total expense for user
    /**
     * Calculates the grand total of all expense transactions for a user.
     *
     * @param userId the unique ID of the user
     * @return total expense amount
     */
    public double getTotalExpense(int userId) {
        List<Transaction> all = db.fetchTransactions(userId);
        if (all == null) {
            return 0;
        }
        return all.stream()
                .filter(t -> t.getType().equals("expense"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // returns categoryId with highest spending
    /**
     * Identifies the category where the user has spent the most money.
     *
     * @param userId the unique ID of the user
     * @return the categoryId with the highest total expenses, or -1 if no
     * expenses exist
     */
    public int getTopSpendingCategory(int userId) {
        Map<Integer, Double> byCategory = getExpensesByCategory(userId);
        if (byCategory.isEmpty()) {
            return -1;
        }
        return Collections.max(byCategory.entrySet(),
                Map.Entry.comparingByValue()).getKey();
    }
}
