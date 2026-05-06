package com.budgetapp.model;

import java.util.Date;

/**
 * Represents an expense transaction. Extends {@link Transaction} and adds a
 * payment method field (e.g., Cash, Credit Card).
 *
 * @version 1.0
 */
public class Expense extends Transaction {

    /**
     * The method used to pay for the expense (e.g., "Cash", "Credit Card",
     * "Debit"). This helps users track their liquidity across different
     * accounts.
     */
    private String paymentMethod;

    /**
     * Full constructor for existing expenses (typically retrieved from the
     * database).
     *
     * @param id the unique transaction ID
     * @param userId the owner of the expense
     * @param categoryId the associated category (e.g., Food, Rent)
     * @param amount the monetary value
     * @param date the date of the transaction
     * @param description a short note about the purchase
     * @param paymentMethod the source of funds
     */
    public Expense(int id, int userId, int categoryId, double amount, Date date, String description, String paymentMethod) {
        super(id, userId, categoryId, amount, date, description);
        this.paymentMethod = paymentMethod;
    }

    /**
     * Overloaded constructor for new expenses where the ID is not yet
     * generated.
     */
    public Expense(int userId, double amount, Date date, String description,
            int categoryId, String source, String paymentMethod) {
        this(0, userId, categoryId, amount, date, description, paymentMethod);
    }

    /**
     * Overloaded constructor that accepts a String date, convenient for UI or
     * CSV inputs.
     *
     * @param dateStr the date in YYYY-MM-DD format
     */
    public Expense(int id, int userId, int categoryId, double amount, String dateStr, String description, String paymentMethod) {
        this(id, userId, categoryId, amount, java.sql.Date.valueOf(dateStr), description, paymentMethod);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Identifies the nature of this transaction for UI rendering and reporting.
     *
     * @return a constant string "expense"
     */
    @Override
    public String getType() {
        return "expense";
    }
    public String getExtra() { return paymentMethod; }
}
