package com.budgetapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.budgetapp.observer.IBudgetObserver;

/**
 * Manages user budgets and spending limits per category. Implements the
 * Observer pattern — notifies registered
 * {@link com.budgetapp.observer.IBudgetObserver} instances when spending
 * exceeds the limit.
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public class Budget {

    /**
     * Unique identifier for the budget record in the database.
     */
    private int budgetId;

    /**
     * The user ID to whom this budget belongs.
     */
    private int userId;

    /**
     * The ID of the transaction category (e.g., Food, Rent) this budget tracks.
     */
    private int categoryId;

    /**
     * The maximum spending limit allowed for this category.
     */
    private double limit;

    /**
     * The current cumulative spending for this budget period.
     */
    private double currentSpent;

    /**
     * The start date for the budget cycle.
     */
    private Date startDate;

    /**
     * The expiration date for the budget cycle.
     */
    private Date endDate;

    /**
     * Registry of listeners to be notified if the budget is exceeded.
     */
    private final List<IBudgetObserver> observers = new ArrayList<>();

    /**
     * <p>
     * Constructor for Budget.</p>
     *
     * @param budgetId a int
     * @param userId a int
     * @param categoryId a int
     * @param limit a double
     * @param currentSpent a double
     * @param startDate a {@link java.util.Date} object
     * @param endDate a {@link java.util.Date} object
     */
    public Budget(int budgetId, int userId, int categoryId, double limit, double currentSpent, Date startDate, Date endDate) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.limit = limit;
        this.currentSpent = currentSpent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters 
    /**
     * <p>
     * Getter for the field <code>budgetId</code>.</p>
     *
     * @return a int
     */
    public int getBudgetId() {
        return budgetId;
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
     * Getter for the field <code>categoryId</code>.</p>
     *
     * @return a int
     */
    public int getCategoryId() {
        return categoryId;
    }


    /**
     * <p>
     * Getter for the field <code>limit</code>.</p>
     *
     * @return a double
     */
    public double getLimit() {
        return limit;
    }

    /**
     * <p>
     * Setter for the field <code>limit</code>.</p>
     *
     * @param limit a double
     */
    public void setLimit(double limit) {
        if (limit >= 0) {
            this.limit = limit;
        } else {
            throw new IllegalArgumentException("Limit must be non negative");
        }
    }

    /**
     * <p>
     * Getter for the field <code>currentSpent</code>.</p>
     *
     * @return a double
     */
    public double getCurrentSpent() {
        return currentSpent;
    }



    /**
     * <p>
     * Getter for the field <code>startDate</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getStartDate() {
        return startDate;
    }


    /**
     * <p>
     * Getter for the field <code>endDate</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getEndDate() {
        return endDate;
    }



   /**
     * Increments the current spending amount and triggers a limit check.
     *
     * @param amount the value of the new expense to add
     */
    public void updateSpent(double amount) {
        if (amount > 0) {
            this.currentSpent += amount;
            
            // Trigger notification if the limit is exceeded
            if (this.currentSpent > this.limit) {
                notifyObservers();
            }
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }


    /**
     * Checks if the total spending has surpassed the defined limit. If
     * exceeded, all registered observers are notified immediately.
     */





    // Observer pattern
    /**
     * Registers a new observer (e.g., a notification service) to watch this
     * budget.
     *
     * @param observer the listener to add
     */
    public void addObserver(IBudgetObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }


//    /**
//     * Iterates through all observers and triggers their update method. This is
//     * called automatically by {@link #checkLimit()}.
//     */
    /**
     * <p>notifyObservers.</p>
     */
    public void notifyObservers() {
        for (IBudgetObserver observer : observers) {
            observer.updateAlert(this);
        }
    }
}
