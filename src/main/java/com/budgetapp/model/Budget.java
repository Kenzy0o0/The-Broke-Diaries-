package com.budgetapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.budgetapp.observer.IBudgetObserver;

/**
 * Manages user budgets and spending limits per category. Implements the
 * Observer pattern — notifies registered {@link IBudgetObserver} instances when
 * spending exceeds the limit.
 *
 * @version 1.0
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
    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        if (limit >= 0) {
            this.limit = limit;
        } else {
            throw new IllegalArgumentException("Limit must be non negative");
        }
    }

    public double getCurrentSpent() {
        return currentSpent;
    }

    public void setCurrentSpent(double currentSpent) {
        if (currentSpent >= 0) {
            this.currentSpent = currentSpent;
            checkLimit();
        } else {
            throw new IllegalArgumentException("Current spent must be non negative");
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Increments the current spending amount and triggers a limit check.
     *
     * @param amount the value of the new expense to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void updateSpent(double amount) {
        if (amount > 0) {
            this.currentSpent += amount;
            checkLimit();
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    /**
     * Checks if the total spending has surpassed the defined limit. If
     * exceeded, all registered observers are notified immediately.
     */
    private void checkLimit() {
        if (currentSpent > limit) {
            notifyObservers();
        }
    }

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

    /**
     * Removes a registered observer so it no longer receives alerts.
     *
     * @param observer the listener to remove
     */
    public void removeObserver(IBudgetObserver observer) {
        observers.remove(observer);
    }

    /**
     * Iterates through all observers and triggers their update method. This is
     * called automatically by {@link #checkLimit()}.
     */
    public void notifyObservers() {
        for (IBudgetObserver observer : observers) {
            observer.updateAlert(this);
        }
    }
}
