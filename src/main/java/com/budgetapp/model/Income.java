package com.budgetapp.model;

import java.util.Date;

/**
 * Represents an income transaction. Extends
 * {@link com.budgetapp.model.Transaction} and adds a source field (e.g.,
 * Salary, Freelance).
 *
 * @version 1.0
 * @author WeDon'tHave
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
     *
     * @param userId a int
     * @param amount a double
     * @param date a {@link java.util.Date} object
     * @param description a {@link java.lang.String} object
     * @param source a {@link java.lang.String} object
     * @param categoryId a int
     */
    public Income(int userId, double amount, Date date, String description, String source, int categoryId) {
        this(0, userId, categoryId, amount, date, description, source);
    }

    /**
     * Convenience constructor that parses a date string.
     *
     * @param dateStr date in "YYYY-MM-DD" format
     * @param id a int
     * @param userId a int
     * @param categoryId a int
     * @param amount a double
     * @param description a {@link java.lang.String} object
     * @param source a {@link java.lang.String} object
     */
    public Income(int id, int userId, int categoryId, double amount, String dateStr, String description, String source) {
        this(id, userId, categoryId, amount, java.sql.Date.valueOf(dateStr), description, source);
    }

    /**
     * <p>
     * Getter for the field <code>source</code>.</p>
     *
     * @return the source of this income.
     */
    public String getSource() {
        return source;
    }


    /**
     * {@inheritDoc}
     *
     * Distinguishes this transaction type for reporting and filtering.
     */
    @Override
    public String getType() {
        return "income";
    }

    /**
     * <p>
     * getExtra.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getExtra() {
        return source;
    }
}
