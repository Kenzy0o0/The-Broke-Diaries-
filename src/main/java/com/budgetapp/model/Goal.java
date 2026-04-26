package com.budgetapp.model;
import java.util.Date;

public class Goal {
    private int id;
    private int userId;
    private double currentSavedAmount;
    private double targetAmount;
    private Date deadline;

    public Goal( int userId, double targetAmount, Date deadline) {
        this.userId = userId;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.currentSavedAmount = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getCurrentSavedAmount() { return currentSavedAmount; }
    public double getRemainingAmount() { return targetAmount - currentSavedAmount;}

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) {
        if (targetAmount >= 0) {
            this.targetAmount = targetAmount;
        } else {
            throw new IllegalArgumentException("Target amount must be non-negative");
        }
    }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public void addFunds(double amount) {
        if (amount > 0) {
            this.currentSavedAmount += amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

     public boolean isAchieved() { return currentSavedAmount >= targetAmount;}
    
}
