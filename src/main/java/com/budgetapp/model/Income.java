package com.budgetapp.model;

import java.util.Date;

/**
 * Represents an income transaction. Extends {@link Transaction} and adds a
 * source field (e.g., Salary, Freelance).
 *
 * @version 1.0
 */
public class Income extends Transaction {

    /**
     * The origin of the funds (e.g., "Salary", "Gift", "Freelance"). This field
     * allows for granular tracking of different revenue streams.
     */
    private String source;

    /**
     * Full constructor for existing income records.
     *
     * @param id the unique transaction ID from the database
     * @param userId the ID of the user receiving the income
     * @param categoryId the category link (e.g., "Professional Services")
     * @param amount the total value of the income
     * @param date the date received
     * @param description additional notes or context
     * @param source the specific entity or origin of the payment
     */
    public Income(int id, int userId, int categoryId, double amount, Date date, String description, String source) {
        super(id, userId, categoryId, amount, date, description);
        this.source = source;
    }

    /**
     * Helper constructor for creating a new Income entry before it has a
     * database ID.
     */
    public Income(int userId, double amount, Date date, String description, String source, int categoryId) {
        this(0, userId, categoryId, amount, date, description, source);
    }

    /**
     * Convenience constructor that parses a date string.
     *
     * @param dateStr date in "YYYY-MM-DD" format
     */
    public Income(int id, int userId, int categoryId, double amount, String dateStr, String description, String source) {
        this(id, userId, categoryId, amount, java.sql.Date.valueOf(dateStr), description, source);
    }

    /**
     * @return the source of this income.
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the new origin string for this income.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Distinguishes this transaction type for reporting and filtering.
     *
     * @return a constant string "income"
     */
    @Override
    public String getType() {
        return "income";
    }
}
