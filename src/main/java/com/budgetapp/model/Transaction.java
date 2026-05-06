package com.budgetapp.model;

import java.util.Date;

/**
 * Abstract base class for all financial transactions. Subclasses {@link Income}
 * and {@link Expense} extend this class to represent the two types of
 * transactions.
 *
 * @version 1.0
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    /**
     * Updates the transaction amount with validation.
     *
     * @param amount the new value
     * @throws IllegalArgumentException if amount is zero or negative
     */
    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Polymorphic method to identify the subclass type. Must be implemented by
     * {@link Income} and {@link Expense}.
     *
     * @return a String representing the transaction type ("income" or
     * "expense")
     */
    public abstract String getType();

}
