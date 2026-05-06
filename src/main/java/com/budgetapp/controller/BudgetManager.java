package com.budgetapp.controller;

import java.util.Date;
import java.util.List;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;

public class BudgetManager {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public void createBudget(int userId, double limit, int categoryId, Date startDate, Date endDate) {
        Budget b = new Budget( 0,userId,categoryId,limit,0,startDate,endDate);

        b.addObserver(new NotificationManager());

        boolean saved = db.saveBudget(b);
        if (saved) {
            System.out.println("Budget created");
        } else {
            System.out.println("Budget creation failed");
        }
    }

    public void editBudget(int budgetId, double newLimit) {
        Budget b = db.fetchBudget(budgetId);
        if (b == null) {
            System.out.println("Budget not found");
            return;
        }
        b.setLimit(newLimit);
        db.updateBudget(b);
        System.out.println("Budget updated");
    }

    public List<Budget> getBudgets(int userId) {
        return db.fetchBudgets(userId);
    }

    public void deleteBudget(int budgetId) {
        db.deleteBudget(budgetId);
    }

    public void updateBudgetSpent(int userId, int categoryId, double amount) {
        List<Budget> budgets = db.fetchBudgets(userId);
        if (budgets == null) return;
        for (Budget b : budgets) {
            if (b.getCategoryId() == categoryId) {
                b.addObserver(new NotificationManager());
                b.updateSpent(amount);
                db.updateBudget(b);
                return;
            }
        }
    }
}