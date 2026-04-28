package com.budgetapp.model;
import com.budgetapp.observer.IBudgetObserver;
import java.util.List;
import java.util.ArrayList;


public class Budget {
    private int id;
    private int userId;
    private double limit;
    private double currentSpent;
    private final List<IBudgetObserver> observers = new ArrayList<>();

    public Budget( int userId, double limit) {
        this.userId = userId;
        this.limit = limit;
        this.currentSpent = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getLimit() { return limit; }
    public void setLimit(double limit) {
        if (limit >= 0) {
            this.limit = limit;
        } else {
            throw new IllegalArgumentException("Limit must be non-negative");
        }
    }

    public double getCurrentSpent() { return currentSpent; }

    public void updateSpent(double amount) {
        if (amount > 0) {
            this.currentSpent += amount;
            checklimit();
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public void checklimit() {
        if (currentSpent > limit) {
           notifyObservers();
        }
    }

    public void AddObserver(IBudgetObserver observer) {
        this.observers.add(observer);
    }

    public void notifyObservers() {
        for (IBudgetObserver observer : observers) {
            observer.updateAlert(this);
        }
    }
}
