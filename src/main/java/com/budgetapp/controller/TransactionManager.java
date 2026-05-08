package com.budgetapp.controller;

import java.util.Date;
import java.util.List;

import com.budgetapp.factory.TransactionFactory;
import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Category;
import com.budgetapp.model.Transaction;
import com.budgetapp.model.User;

/**
 * The central controller responsible for managing the lifecycle of financial
 * transactions.
 *
 * This class implements the Singleton pattern to provide a global point of
 * access for transaction-related operations. It orchestrates complex business
 * logic that spans multiple domain areas, including:
 *
 * <ul>
 * <li><b>Factory Integration:</b> Utilizing
 * {@link com.budgetapp.factory.TransactionFactory} to instantiate polymorphic
 * Income and Expense objects.</li>
 * <li><b>Balance Synchronization:</b> Automatically updating
 * {@link com.budgetapp.model.User} balances whenever a transaction is
 * successfully persisted.</li>
 * <li><b>Budget Integration:</b> Communicating with
 * {@link com.budgetapp.controller.BudgetManager} to track real-time spending
 * against defined limits.</li>
 * <li><b>Data Aggregation:</b> Calculating monthly financial summaries and
 * handling paginated data retrieval for the UI.</li>
 * </ul>
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public class TransactionManager {

    private final DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Orchestrates the creation of a new transaction. 1. Uses
     * TransactionFactory to create the correct object type. 2. Persists the
     * transaction to the database. 3. Updates the User's overall balance. 4. If
     * it's an expense, triggers a budget limit check via BudgetManager.
     *
     * @param userId a int
     * @param type a {@link java.lang.String} object
     * @param amount a double
     * @param cat a {@link com.budgetapp.model.Category} object
     * @param date a {@link java.util.Date} object
     * @param description a {@link java.lang.String} object
     * @param extra a {@link java.lang.String} object
     */
  /**
 * @return A success flag (true if added, false if input/DB errors)
 */
public boolean addTransaction(int userId, String type, double amount, Category cat, Date date, String description, String extra) {
    // Input validation...
    if (amount <= 0) return false;
    if (cat == null) return false;
    if (type == null || (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense"))) return false;
    if (description == null || description.trim().isEmpty()) return false;
    if (date == null || date.after(new Date())) return false;

    User user = db.fetchUser(userId);
    if (user == null) return false;

    // 1. Validate Balance and Budget limits FIRST
    if (type.equalsIgnoreCase("expense")) {
        if (user.getBalance() < amount) {
            System.out.println("Insufficient funds");
            return false;
        }
        
        BudgetManager bm = new BudgetManager();
        if (bm.wouldExceedBudget(userId, cat.getCategoryId(), amount, date)) {
            System.out.println("Transaction rejected: would exceed budget limit for category " + cat.getName());
            return false; // Transaction is blocked BEFORE saving to the DB
        }
    }

    // Create Transaction Object
    Transaction t;
    if (type.equalsIgnoreCase("income")) {
        t = TransactionFactory.CreateIncome(0, userId, cat.getCategoryId(), amount, date, description, extra);
    } else {
        t = TransactionFactory.CreateExpense(0, userId, cat.getCategoryId(), amount, date, description, extra);
    }

    // 2. Save Transaction to DB
    boolean saved = db.saveTransaction(t);

    if (saved) {
        double change = type.equalsIgnoreCase("income") ? amount : -amount;

        // 3. Update User Balance
        user.updateBalance(change);
        db.updateUser(user);

        // 4. Update Budget Spent Amount (Observer inside will handle notifications)
        if (type.equalsIgnoreCase("expense")) {
            BudgetManager bm = new BudgetManager();
            bm.updateBudgetSpent(userId, cat.getCategoryId(), amount, date);
        }

        System.out.println("Transaction saved successfully");
        return true;
    } else {
        System.out.println("Transaction save failed");
        return false;
    }
}



    /**
     * The single shared instance of the manager.
     */
    private static TransactionManager instance;

    /**
     * Thread-safe method to retrieve the manager instance.
     *
     * @return the singleton TransactionManager
     */
    public static synchronized TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    /**
     * Calculates the start and end of the current calendar month and fetches
     * the sum of all income transactions for the user within that range.
     *
     * @param currentUser a {@link com.budgetapp.model.User} object
     * @return a double
     */
    public double getTotalIncomeThisMonth(User currentUser) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(java.util.Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        List<Double> totals = db.fetchTotalIncomeExpenseBetween(currentUser.getId(), startDate, endDate);

        if (totals == null) {
            return 0;
        }
        return totals.get(0);
    }

    /**
     * Calculates the current month's range and returns the total spent.
     *
     * @param currentUser a {@link com.budgetapp.model.User} object
     * @return a double
     */
    public double getTotalExpenseThisMonth(User currentUser) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(java.util.Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        List< Double> totals = db.fetchTotalIncomeExpenseBetween(currentUser.getId(), startDate, endDate);

        if (totals == null) {
            return 0;
        }
        return totals.get(1);
    }

    /**
     * <p>
     * getCurrentBalance.</p> * @param currentUser a
     * {@link com.budgetapp.model.User} object
     *
     * @return a double
     * @param currentUser a {@link com.budgetapp.model.User} object
     */
    public double getCurrentBalance(User currentUser) {
        User fresh = db.fetchUser(currentUser.getId());
        return fresh != null ? fresh.getBalance() : 0;
    }
}
