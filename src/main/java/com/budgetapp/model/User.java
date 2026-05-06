package com.budgetapp.model;

/**
 * Represents a registered user of the budgeting application. Stores profile
 * information including name, currency preference, and current balance.
 *
 * @version 1.0
 */
public class User {

    /**
     * The unique database primary key for the user.
     */
    private int id;

    /**
     * The user's display name or nickname.
     */
    private String name;

    /**
     * The current liquid total available across all accounts.
     */
    private double balance;

    /**
     * The preferred currency code (e.g., "USD", "EUR") for display formatting.
     */
    private String currency;

    /**
     * Constructor for new users. ID defaults to 0 until persisted.
     *
     * @param n User's name
     * @param b Initial balance
     * @param c Currency preference
     */
    public User(String n, double b, String c) {
        name = n;
        balance = b;
        currency = c;
    }

    /**
     * Constructor for existing users retrieved from storage.
     *
     * @param i Existing unique ID
     * @param n User's name
     * @param b Current balance
     * @param c Currency preference
     */
    public User(int i, String n, double b, String c) {
        id = i;
        name = n;
        balance = b;
        currency = c;
    }

    /**
     * Adjusts the user's balance by a positive (income) or negative (expense)
     * amount. Prevents the balance from falling below zero.
     *
     * @param amount The value to add or subtract
     * @return true if the balance was successfully updated, false if the
     * operation would result in a negative balance.
     */
    public boolean updateBalance(double amount) {
        if (balance + amount >= 0) {
            balance += amount;
            return true;
        }
        return false;
    }

    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
