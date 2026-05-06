package com.budgetapp.observer;

import com.budgetapp.model.Budget;

/**
 * Observer interface for budget limit alerts. Any class that wants to receive
 * budget exceeded notifications must implement this interface.
 *
 * @version 1.0
 */
public interface IBudgetObserver {

    /**
     * Called when a budget's spending exceeds its defined limit.
     *
     * @param budget the specific {@link Budget} instance that triggered the
     * alert. Implementation classes can use this to access the current spent
     * amount, the limit, and the category name.
     */
    void updateAlert(Budget budget);
}
