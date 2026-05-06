package com.budgetapp.model;

import java.util.Date;

/**
 * Abstract base class for all financial transactions. Subclasses
 * {@link com.budgetapp.model.Income} and {@link com.budgetapp.model.Expense}
 * extend this class to represent the two types of transactions.
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public abstract class Transaction {

    /**
     * The unique database primary key for the transaction record.
     */
    private int id;

    /**
     * The foreign key referencing the {@link User} who owns this record.
     */
    private int userId;

    /**
     * The foreign key referencing the {@link Category} (e.g., Food, Salary).
     */
    private int categoryId;

    /**
     * The monetary value of the transaction. Must be a positive value.
     */
    private double amount;

    /**
     * The timestamp of when the transaction occurred.
     */
    private Date date;

    /**
     * A user-provided note or memo describing the transaction.
     */
    private String description;

    /**
     * Protected constructor to be called by subclasses (Income/Expense).
     *
     * @param id Unique record ID
     * @param userId Owner ID
     * @param categoryId Category reference
     * @param amount Monetary value
     * @param date Transaction date
     * @param description Memo/Note
     */
    public Transaction(int id, int userId, int categoryId, double amount, Date date, String description) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters and setters
    /**
     * <p>
     * Getter for the field <code>id</code>.</p>
     *
     * @return a int
     */
    public int getId() {
        return id;
    }

    /**
     * <p>
     * Setter for the field <code>id</code>.</p>
     *
     * @param id a int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * <p>
     * Getter for the field <code>userId</code>.</p>
     *
     * @return a int
     */
    public int getUserId() {
        return userId;
    }

    /**
     * <p>
     * Setter for the field <code>userId</code>.</p>
     *
     * @param userId a int
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * <p>
     * Getter for the field <code>amount</code>.</p>
     *
     * @return a double
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Updates the transaction amount with validation.
     *
     * @param amount the new value
     * @throws java.lang.IllegalArgumentException if amount is zero or negative
     */
    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    /**
     * <p>
     * Getter for the field <code>categoryId</code>.</p>
     *
     * @return a int
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * <p>
     * Setter for the field <code>categoryId</code>.</p>
     *
     * @param categoryId a int
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * <p>
     * Getter for the field <code>date</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getDate() {
        return date;
    }

    /**
     * <p>
     * Setter for the field <code>date</code>.</p>
     *
     * @param date a {@link java.util.Date} object
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * <p>
     * Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>
     * Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Polymorphic method to identify the subclass type. Must be implemented by
     * {@link com.budgetapp.model.Income} and
     * {@link com.budgetapp.model.Expense}.
     *
     * @return a String representing the transaction type ("income" or
     * "expense")
     */
    public abstract String getType();

    /**
     * <p>
     * getExtra.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public abstract String getExtra();

}
