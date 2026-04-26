package com.budgetapp.model;

public class Budget {

    public Budget(Integer userId, Integer categoryId, double limitAmount, double currentSpent, String startDate, String endDate) {
        // Constructor implementation
    }

    //second constructor
    public Budget(Integer bId, Integer userId, Integer categoryId, double limitAmount, double currentSpent, String startDate, String endDate) {
        // Constructor implementation
    }

    // Integer categoryId, double limitAmount, double currentSpent, String startDate, String endDate) {
    public boolean setCategoryId(Integer categoryId) {
        return true;
    }

    public boolean setLimitAmount(double limitAmount) {
        return true;
    }

    public boolean setCurrentSpent(double currentSpent) {
        return true;
    }

    public boolean setStartDate(String startDate) {
        return true;
    }

    public boolean setEndDate(String endDate) {
        return true;
    }

    public Integer getUserId() {
        return 1;
    }

    public Integer getCategoryId() {
        return 1;
    }

    public double getLimitAmount() {
        return 1000;
    }

    public double getCurrentSpent() {
        return 500;
    }

    public String getStartDate() {
        return "2023-01-01";
    }

    public String getEndDate() {
        return "2023-12-31";
    }
}
