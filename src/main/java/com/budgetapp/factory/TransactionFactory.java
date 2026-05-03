package com.budgetapp.factory;

import java.util.Date;

import com.budgetapp.model.Income;
import com.budgetapp.model.Transaction;

public class TransactionFactory {

  public static Transaction CreateIncome(int id, int userId,int categoryId, double amount, Date date, String description, String source)
  {
    return new Income(id, userId, categoryId, amount, date, description, source);
  }

  public static Transaction CreateExpense(int id, int userId,int categoryId, double amount, Date date, String description, String paymentMethod)
{
  return new com.budgetapp.model.Expense(id, userId, categoryId, amount, date, description, paymentMethod);
}

}