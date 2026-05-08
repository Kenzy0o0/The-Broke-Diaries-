package com.budgetapp.model;

import java.util.Date;

/**
 * Represents an expense transaction. Extends
 * {@link com.budgetapp.model.Transaction} and adds a payment method field
 * (e.g., Cash, Credit Card).
 *
 * @version 1.0
 * @author WeDon'tHave
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
     *
     * @param userId a int
     * @param amount a double
     * @param date a {@link java.util.Date} object
     * @param description a {@link java.lang.String} object
     * @param categoryId a int
     * @param source a {@link java.lang.String} object
     * @param paymentMethod a {@link java.lang.String} object
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
     * @param id a int
     * @param userId a int
     * @param categoryId a int
     * @param amount a double
     * @param description a {@link java.lang.String} object
     * @param paymentMethod a {@link java.lang.String} object
     */
    public Expense(int id, int userId, int categoryId, double amount, String dateStr, String description, String paymentMethod) {
        this(id, userId, categoryId, amount, java.sql.Date.valueOf(dateStr), description, paymentMethod);
    }

    /**
     * <p>
     * Getter for the field <code>paymentMethod</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * {@inheritDoc}
     *
     * Identifies the nature of this transaction for UI rendering and reporting.
     */
    @Override
    public String getType() {
        return "expense";
    }

    /**
     * <p>
     * getExtra.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getExtra() {
        return paymentMethod;
    }
}
