package com.budgetapp.controller;

import java.util.Date;
import java.util.List;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;
import com.budgetapp.model.User;

/**
 * Controller class that manages the lifecycle of
 * {@link com.budgetapp.model.Budget} objects.
 *
 * This manager coordinates between the data layer (DatabaseManager) and the
 * notification system (NotificationManager). It ensures that budgets are
 * correctly observed for limit violations whenever they are created or updated.
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public class BudgetManager {

    /**
     * The observer implementation that creates
     * {@link com.budgetapp.model.Notification} objects when a budget is
     * exceeded.
     */
    private final NotificationManager notificationManager = new NotificationManager();
    /**
     * Singleton instance for persistent storage operations.
     */
    private final DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Creates a new budget, attaches a notification observer, and persists it.
     *
     * @param userId the owner of the budget
     * @param limit the maximum spending threshold
     * @param categoryId the category to monitor
     * @param startDate beginning of the budget cycle
     * @param endDate end of the budget cycle
     */
    public void createBudget(int userId, double limit, int categoryId, Date startDate, Date endDate) {
        Budget b = new Budget(0, userId, categoryId, limit, 0, startDate, endDate);

        b.addObserver(notificationManager);

        boolean saved = db.saveBudget(b);
        if (saved) {
            System.out.println("Budget created");
        } else {
            System.out.println("Budget creation failed");
        }
    }

    /**
     * Modifies the spending limit of an existing budget.
     *
     * @param budgetId the unique ID of the budget to edit
     * @param newLimit the updated threshold value
     */
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

    /**
     * <p>
     * getBudgets.</p>
     *
     * @param userId a int
     * @return a {@link java.util.List} object
     */
    public List<Budget> getBudgets(int userId) {
        return db.fetchBudgets(userId);
    }

    /**
     * <p>
     * deleteBudget.</p>
     *
     * @param budgetId a int
     */
    public void deleteBudget(int budgetId) {
        db.deleteBudget(budgetId);
    }

    /**
     * Logic to update spending totals whenever a new transaction occurs. It
     * searches for an active budget matching the user and category, triggers
     * the internal spending update, and saves the new total.
     *
     * @param userId the user performing the transaction
     * @param categoryId the category of the expense
     * @param amount the cost to add to the budget
     * @param transactionDate the date of the transaction to match against budget cycles
     */

    // only update budgets whose cycle includes the transaction date
   public void updateBudgetSpent(int userId, int categoryId, double amount, Date transactionDate) {
    List<Budget> budgets = db.fetchBudgets(userId);
    User user = db.fetchUser(userId);
    double balance = user.getBalance();
    if (budgets == null) {
        return;
    }
    boolean updated = false;
    for (Budget b : budgets) {
        if (b.getCategoryId() == categoryId
                && !transactionDate.before(b.getStartDate())
                && !transactionDate.after(b.getEndDate())) {
            b.addObserver(notificationManager);
            b.updateSpent(amount, balance);
            db.updateBudget(b);
            updated = true;
        }
    }
    if (!updated) {
        System.out.println("No active budget matched for this transaction's date and category.");
    }
}
}
