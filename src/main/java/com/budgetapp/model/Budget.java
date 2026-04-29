package com.budgetapp.model;

import com.budgetapp.observer.IBudgetObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Budget {
    private int budgetId;
    private int userId;
    private int categoryId;
    private double limit;
    private double currentSpent;
    private Date startDate;
    private Date endDate;
    private final List<IBudgetObserver> observers = new ArrayList<>();

    
    public Budget( int budgetId, int userId, int categoryId,  double limit, double currentSpent, Date startDate, Date endDate) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.limit = limit;
        this.currentSpent = currentSpent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters 
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public double getLimit() { return limit; }
    public void setLimit(double limit) {
        if (limit >= 0) this.limit = limit;
        else throw new IllegalArgumentException("Limit must be non negative");
    }

    public double getCurrentSpent() { return currentSpent; }
    public void setCurrentSpent(double currentSpent) {
        if (currentSpent >= 0) {
            this.currentSpent = currentSpent;
            checkLimit();
        } else throw new IllegalArgumentException("Current spent must be non negative");
    }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    
    public void updateSpent(double amount) {
        if (amount > 0) {
            this.currentSpent += amount;
            checkLimit();
        } else throw new IllegalArgumentException("Amount must be positive");
    }

    private void checkLimit() {
        if (currentSpent > limit) notifyObservers();
    }

    // Observer pattern
    public void addObserver(IBudgetObserver observer) {
        if (observer != null && !observers.contains(observer))
            observers.add(observer);
    }

    public void removeObserver(IBudgetObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (IBudgetObserver observer : observers)
            observer.updateAlert(this);
    }
}