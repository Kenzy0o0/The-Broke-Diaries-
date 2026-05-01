package com.budgetapp.controller;

import com.budgetapp.model.User;

public class TransactionManager {

    // these should use the methods that gets the total income and balance that are present in the dbManager
    // since calculating the sum in sql is much safer and efficient than doing it in the program itself
    // use fetchTotalIncomeExpenseBetween(java.util.Date startDate, java.util.Date endDate)
    // this function return a list, [0] is totalincome in a specific time range
    // [1] is teh expense
    //! this is not complete, this is just a temp solution to test the dashboard
    //! i don't know how to implement this yet
    public static synchronized TransactionManager getInstance() {
        return new TransactionManager();
    }

    public int getCurrentBalance(User currentUser) {
        return 100;
    }

    public int getTotalIncomeThisMonth(User currentUser) {
        return 200;
    }

    public int getTotalExpenseThisMonth(User currentUser) {
        return 100;
    }

}
