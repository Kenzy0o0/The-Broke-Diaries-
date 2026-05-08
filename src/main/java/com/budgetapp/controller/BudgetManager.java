package com.budgetapp.controller;

import java.util.Date;
import java.util.List;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;

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
     * Checks if adding an expense would exceed the active budget limit for a category.
     * Triggers a notification popup if the limit is exceeded.
     *
     * @param userId a int
     * @param categoryId a int
     * @param amount a double
     * @param transactionDate a {@link java.util.Date} object
     * @return a boolean
     */
    public boolean wouldExceedBudget(int userId, int categoryId, double amount, Date transactionDate) {
        List<Budget> budgets = db.fetchBudgets(userId);
        if (budgets == null) return false;

        for (Budget b : budgets) {
            if(b.getCategoryId() == categoryId && !transactionDate.before(b.getStartDate()) && !transactionDate.after(b.getEndDate())) {
                double newSpent = b.getCurrentSpent() + amount;
                
                if (newSpent > b.getLimit()) {
                    // 1. Attach the NotificationManager to listen for the alert
                    b.addObserver(notificationManager);
                    
                    // 2. Call updateSpent in memory ONLY. 
                    // This increments the amount and triggers the popup notification.
                    // Because we return true immediately after, the database is NEVER updated with this exceeded amount.
                    b.updateSpent(amount); 
                    
                    // 3. Return true to signal TransactionManager to block the transaction
                    return true; 
                }
            }
        }
        return false; 
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
        if (budgets == null) return;
        
        boolean updated = false;
        for (Budget b : budgets) {
            if (b.getCategoryId() == categoryId && !transactionDate.before(b.getStartDate()) && !transactionDate.after(b.getEndDate())) {
                b.updateSpent(amount); // Removed the 'balance' argument here
                db.updateBudget(b);
                updated = true;
            }
        }
        if (!updated) {
            System.out.println("No active budget matched for this transaction's date and category.");
        }
    }
}
