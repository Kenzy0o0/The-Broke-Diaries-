package com.budgetapp.controller;

import java.util.Date;
import java.util.List;

import com.budgetapp.factory.TransactionFactory;
import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Category;
import com.budgetapp.model.Transaction;
import com.budgetapp.model.User;

public class TransactionManager {

    public void addTransaction(int userId, String type, double amount,
                               Category cat, Date date,
                               String description, String extra) {
        Transaction t;

        if (type.equalsIgnoreCase("income")) {
            t = TransactionFactory.CreateIncome(
                0, userId, cat.getCategoryId(), amount, date, description, extra
            );
        } else {
            t = TransactionFactory.CreateExpense(
                0, userId, cat.getCategoryId(), amount, date, description, extra
            );
        }

        boolean saved = DatabaseManager.getInstance().saveTransaction(t);

        if (saved) {
            User user = DatabaseManager.getInstance().fetchUser(userId);
            if (user != null) {
                double change = type.equalsIgnoreCase("income") ? amount : -amount;
                user.updateBalance(change);
                DatabaseManager.getInstance().updateUser(user);
            }

            if (type.equalsIgnoreCase("expense")) {
                BudgetManager bm = new BudgetManager();
                bm.updateBudgetSpent(userId, cat.getCategoryId(), amount);
            }

            System.out.println("Transaction saved successfully");
        } else {
            System.out.println("Transaction save failed");
        }
    }

    public List<Transaction> getTransactions(int userId, int limit, int offset) {
        List<Transaction> all = DatabaseManager.getInstance().fetchTransactions(userId);
        if (all == null) return null;
        int fromIndex = Math.min(offset, all.size());
        int toIndex   = Math.min(offset + limit, all.size());
        return all.subList(fromIndex, toIndex);
    }

    public static TransactionManager getInstance() {
        return new TransactionManager();
    }

    public void deleteTransaction(int transactionId) {
        boolean deleted = DatabaseManager.getInstance().deleteTransaction(transactionId);
        if (deleted) System.out.println("Transaction deleted");
        else         System.out.println("Transaction delete failed");
    }

    public void deactivateCategory(int categoryId) {
        List<Category> categories = DatabaseManager.getInstance().fetchCategories(0);
        if (categories == null) return;
        for (Category c : categories) {
            if (c.getCategoryId() == categoryId) {
                c.deactivate();
                DatabaseManager.getInstance().updateCategory(c);
                System.out.println("Category deactivated: " + c.getName());
                return;
            }
        }
    }

    public double getTotalIncomeThisMonth(User currentUser) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(java.util.Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        List<Double> totals = DatabaseManager.getInstance()
            .fetchTotalIncomeExpenseBetween(currentUser.getId(), startDate, endDate); 

        if (totals == null) return 0;
        return totals.get(0);
    }


    public double getTotalExpenseThisMonth(User currentUser) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(java.util.Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        List<Double> totals = DatabaseManager.getInstance()
            .fetchTotalIncomeExpenseBetween(currentUser.getId(), startDate, endDate); 

        if (totals == null) return 0;
        return totals.get(1);
    }

    public double getCurrentBalance(User currentUser) {
        User fresh = DatabaseManager.getInstance().fetchUser(currentUser.getId());
        return fresh != null ? fresh.getBalance() : 0;
    }

    public List<Transaction> getAllTransactions(int userId) {
        return DatabaseManager.getInstance().fetchTransactions(userId);
    }

    public List<Transaction> getTransactionsByCategory(int userId, int categoryId) {
        return DatabaseManager.getInstance().fetchTransactionsByCategory(userId, categoryId);
    }
}