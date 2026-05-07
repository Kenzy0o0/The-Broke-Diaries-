package com.budgetapp.factory;

import java.util.Date;

import com.budgetapp.model.Expense;
import com.budgetapp.model.Income;
import com.budgetapp.model.Transaction;

/**
 * Factory class for creating Transaction objects. Implements the Factory design
 * pattern to centralize the creation of {@link com.budgetapp.model.Income} and
 * {@link com.budgetapp.model.Expense} objects.
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public class TransactionFactory {

    /**
     * Creates a new Income transaction.
     *
     * @param id the unique transaction ID
     * @param userId the ID of the user who owns this transaction
     * @param categoryId the ID of the associated category
     * @param amount the monetary value of the income
     * @param date the date the income was received
     * @param description a brief note about the income
     * @param source the origin of the income (e.g., "Salary", "Gift")
     * @return a new {@link com.budgetapp.model.Income} instance as a
     * {@link com.budgetapp.model.Transaction}
     */
    public static Transaction CreateIncome(int id, int userId, int categoryId, double amount, Date date, String description, String source) {
        return new Income(id, userId, categoryId, amount, date, description, source);
    }

    /**
     * Creates a new Expense transaction.
     *
     * @param id the unique transaction ID
     * @param userId the ID of the user who owns this transaction
     * @param categoryId the ID of the associated category
     * @param amount the monetary value of the expense
     * @param date the date the expense occurred
     * @param description a brief note about the expense
     * @param paymentMethod the method used (e.g., "Credit Card", "Cash")
     * @return a new Expense instance as a
     * {@link com.budgetapp.model.Transaction}
     */
    public static Transaction CreateExpense(int id, int userId, int categoryId, double amount, Date date, String description, String paymentMethod) {
        return new com.budgetapp.model.Expense(id, userId, categoryId, amount, date, description, paymentMethod);
    }

}
