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
public boolean addTransaction(int userId, String type, double amount,Category cat, Date date,String description, String extra) {
    Transaction t;

    // Input validation
    if (amount <= 0) {
        System.out.println("Amount must be positive");
        return false;
    }
    if (cat == null) {
        System.out.println("Category is required");
        return false;
    }
    if (type == null || (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense"))) {
        System.out.println("Transaction type must be 'income' or 'expense'");
        return false;
    }
    if (description == null || description.trim().isEmpty()) {
        System.out.println("Description is required");
        return false;
    }
    if (date == null) {
        System.out.println("Date is required");
        return false;
    }
    Date now = new Date();
    if (date.after(now)) {
        System.out.println("Date cannot be in the future");
        return false;
    }
    User user = db.fetchUser(userId);
    if (user == null) {
        System.out.println("User not found");
        return false;
    }
    if (type.equalsIgnoreCase("expense") && user.getBalance() < amount) {
        System.out.println("Insufficient funds");
        return false;
    }

    // Create Transaction
    if (type.equalsIgnoreCase("income")) {
        t = TransactionFactory.CreateIncome(0, userId, cat.getCategoryId(), amount, date, description, extra);
    } else {
        t = TransactionFactory.CreateExpense(0, userId, cat.getCategoryId(), amount, date, description, extra);
    }

    boolean saved = db.saveTransaction(t);

   if (saved) {
    double change = type.equalsIgnoreCase("income") ? amount : -amount;

    // BUDGET CHECK for expenses
    if (type.equalsIgnoreCase("expense")) {
        BudgetManager bm = new BudgetManager();
        if (bm.wouldExceedBudget(userId, cat.getCategoryId(), amount, date)) {
            System.out.println("Transaction rejected: would exceed budget limit for category " + cat.getName());
            return false;
        }
    }

    user.updateBalance(change);
    db.updateUser(user);

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
     * Fetches a specific "page" of transactions.
     *
     * @param limit how many transactions to return.
     * @param offset where to start in the list.
     * @return a sublist of transactions for targeted display.
     * @param userId a int
     */
    public List<Transaction> getTransactions(int userId, int limit, int offset) {

        List<Transaction> all = db.fetchTransactions(userId);
        if (all == null) {
            return null;
        }
        int fromIndex = Math.min(offset, all.size());
        int toIndex = Math.min(offset + limit, all.size());
        return all.subList(fromIndex, toIndex);
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
     * <p>
     * deleteTransaction.</p>
     *
     * @param transactionId a int
     */
    public void deleteTransaction(int transactionId) {

        boolean deleted = db.deleteTransaction(transactionId);
        if (deleted) {
            System.out.println("Transaction deleted");
        } else {
            System.out.println("Transaction delete failed");
        }
    }

    /**
     * <p>
     *   * deactivateCategory.</p>
     *
     * @param categoryId a int
     */
    public void deactivateCategory(int categoryId) {
        List<Category> categories = db.fetchCategories(0);
        if (categories == null) {
            return;
        }
        for (Category c : categories) {
            if (c.getCategoryId() == categoryId) {
                c.deactivate();
                db.updateCategory(c);
                System.out.println("Category deactivated: " + c.getName());
                return;
            }
        }
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

    /**
     * <p>
     * getAllTransactions.</p>
     *
     * @param userId a int
     * @return a {@link java.util.List} object
     */
    public List<Transaction> getAllTransactions(int userId) {
        return db.fetchTransactions(userId);
    }

    /**
     * <p>
     * getTransactionsByCategory.</p>
     *
     * @param userId a int
     * @param categoryId a int
     * @return a {@link java.util.List} object
     */
    public List<Transaction> getTransactionsByCategory(int userId, int categoryId) {
        return db.fetchTransactionsByCategory(userId, categoryId);
    }
}
