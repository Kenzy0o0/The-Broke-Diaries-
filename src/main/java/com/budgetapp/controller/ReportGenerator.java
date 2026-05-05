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

public class ReportGenerator {

    private static ReportGenerator instance;
    private DatabaseManager db = DatabaseManager.getInstance();

    private ReportGenerator() {
    }

    public static ReportGenerator getInstance() {
        if (instance == null) {
            instance = new ReportGenerator();
        }
        return instance;
    }

    // returns map of categoryId → total spent (expenses only)
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
    public int getTopSpendingCategory(int userId) {
        Map<Integer, Double> byCategory = getExpensesByCategory(userId);
        if (byCategory.isEmpty()) {
            return -1;
        }
        return Collections.max(byCategory.entrySet(),
                Map.Entry.comparingByValue()).getKey();
    }
}
