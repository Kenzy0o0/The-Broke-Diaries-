package com.budgetapp.infrastructure;

// you can't access subpackages from the parent package, they are just names
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.budgetapp.model.Account;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Category;
import com.budgetapp.model.Notification;
import com.budgetapp.model.Transaction;
import com.budgetapp.model.User;

public class DatabaseManager {

    // to be or not to be, this is the biggest question
    // make it .db to view it
    private static final String URL = "jdbc:sqlite:TheBrokeDiariesDatabase.db";

    // for it to be singleton, it has to has one instance only;
    private static DatabaseManager instance;

    // the constructor would be private for no one to make a new database
    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private static Connection getConnection() throws SQLException {
        // the driver manager is the java sql part that handles connecting to an existing database
        return DriverManager.getConnection(URL);
    }

    public void initializeDatabase() {

        String pragmaSql = "PRAGMA foreign_keys = ON;";
        String[] sqlQueries = {
                //! I MUST DISSCUSSE THE CATEGORY
                "CREATE TABLE IF NOT EXISTS users (uId INTEGER PRIMARY KEY AUTOINCREMENT, fullName TEXT NOT NULL, email TEXT UNIQUE NOT NULL, passwordHash TEXT NOT NULL, currency TEXT DEFAULT 'Pound', language TEXT DEFAULT 'English');",
                // why a seperate table for accouts?
                // "CREATE TABLE IF NOT EXISTS accounts (aId INTEGER PRIMARY KEY AUTOINCREMENT, password TEXT NOT NULL, uId INTEGER UNIQUE NOT NULL, FOREIGN KEY(uId) REFERENCES users(uId) on DELETE CASCADE );",
                "CREATE TABLE IF NOT EXISTS accounts (password TEXT NOT NULL, uId INTEGER PRIMARY KEY, balance REAL DEFAULT 0, FOREIGN KEY(uId) REFERENCES users(uId) on DELETE CASCADE );",
                // if source is null, then it is an expense, if paymentMethod is null, then it is an income
                "CREATE TABLE IF NOT EXISTS transactions (tId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, type TEXT CHECK(type IN ('income', 'expense')) NOT NULL, amount REAL NOT NULL, cId INTEGER,category TEXT, description TEXT, date TEXT DEFAULT (datetime('now')), source TEXT, paymentMethod TEXT, FOREIGN KEY(uId) REFERENCES users(uId), FOREIGN KEY(cId) REFERENCES categories(cId));",
                "CREATE TABLE IF NOT EXISTS budgets (bId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, category TEXT NOT NULL,  limitAmount REAL NOT NULL, currentSpent REAL DEFAULT 0, FOREIGN KEY(uId) REFERENCES users(uId), FOREIGN KEY(cId) REFERENCES categories(cId));",
                "CREATE TABLE IF NOT EXISTS categories (cId INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, isActive BOOLEAN DEFAULT 1);",
                "CREATE TABLE IF NOT EXISTS notifications (nId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, message TEXT NOT NULL, isRead INTEGER DEFAULT 0, date TEXT DEFAULT (datetime('now')), FOREIGN KEY(uId) REFERENCES users(uId));"};

        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute(pragmaSql);
            for (String oneQuery : sqlQueries) {
                st.execute(oneQuery);
            }
        } catch (SQLException e) {
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    public void insertDefaultCategories() {
        String sql = "INSERT OR IGNORE INTO categories (name, type) VALUES (?, ?)";

        String[][] defaults = {
                // expense categories
                {"Food & Dining", "expense"},
                {"Transport", "expense"},
                {"Bills & Utilities", "expense"},
                {"Entertainment", "expense"},
                {"Shopping", "expense"},
                {"Health", "expense"},
                {"Education", "expense"},
                // income categories
                {"Salary", "income"},
                {"Freelance", "income"},
                {"Gift", "income"},
                // both
                {"Other", "both"}
        };

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            for (String[] cat : defaults) {
                ps.setString(1, cat[0]);
                ps.setString(2, cat[1]);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("insertDefaultCategories failed: " + e.getMessage());
        }
    }

    // here we start to use prepared statements, unlike normal statements
    // prepared statements are precompiled sql statement that run with different values each time
    // we use placeholder ? and set them by setString
    //** user
   /* public boolean saveUser(User u) {
        String command = "Insert into users (fullName, email, passwordHash, currency, language) values( ?,  ?,  ?,  ?,  ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getCurrency());
            ps.setString(5, u.getLanguage());

            // we use executeUpdate for insert, update and delete statements, it returns the number of affected rows
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("User save failed: " + e.getMessage());
        }

        return false;
    }*/

    public int saveUser(User u) {
        String command = "Insert into users (fullName, email, passwordHash, currency, language) values( ?,  ?,  ?,  ?,  ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getCurrency());
            ps.setString(5, u.getLanguage());

            // we use executeUpdate for insert, update and delete statements, it returns the number of affected rows
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("User save failed: " + e.getMessage());
        }

        return false;
    }

    public boolean updateUser(User u) {
        String command = "UPDATE users SET fullName = ?, email = ?, passwordHash = ?, currency = ?, language = ? WHERE uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getCurrency());
            ps.setString(5, u.getLanguage());
            ps.setInt(6, u.getId());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("User update failed: " + e.getMessage());
        }
        return false;
    }

    public User fetchUser(int userId) {

        String command = "SELECT * FROM users WHERE uId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);

            ps.executeQuery();

            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("uId"),
                        rs.getString("fullName"),
                        rs.getString("email"),
                        rs.getString("passwordHash"),
                        rs.getString("currency"),
                        rs.getString("language")
                );
                // get returns the attributes of the current row
                return user;
            }

        } catch (SQLException e) {
            System.err.println("User fetch failed: " + e.getMessage());
        }
        return null;
    }

    //** account
    public Account fetchAccount(int userId) {

        String command = "SELECT * FROM accounts WHERE uId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);

            ps.executeQuery();

            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                Account account = new Account(
                        rs.getInt("uId"),
                        rs.getString("password"),
                        rs.getDouble("balance"));
                // get returns the attributes of the current row
                return account;
            }

        } catch (SQLException e) {
            System.err.println("Account fetch failed: " + e.getMessage());
        }
        return null;
    }

    public Account fetchAccountByEmail(String email) {

        String command = "SELECT * FROM accounts WHERE uId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);

            ps.executeQuery();

            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                Account account = new Account(
                        rs.getInt("uId"),
                        rs.getString("password"),
                        rs.getDouble("balance"));
                // get returns the attributes of the current row
                return account;
            }

        } catch (SQLException e) {
            System.err.println("Account fetch failed: " + e.getMessage());
        }
        return null;
    }

    public boolean saveAccount(Account a) {
        String command = "Insert into accounts (password, uId, balance) values(?,?,?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, a.getPassword());
            ps.setInt(2, a.getUserId());
            ps.setDouble(3, a.getBalance());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Account save failed: " + e.getMessage());
        }
        return false;
    }


    public boolean updateAccount(Account a) {
        String command = "UPDATE accounts SET password = ?, balance = ? WHERE uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, a.getPassword());
            ps.setDouble(2, a.getBalance());
            ps.setInt(3, a.getUserId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Account update failed: " + e.getMessage());
        }
        return false;
    }

    //** transaction
    public boolean saveTransaction(Transaction t) {
        String command = "Insert into transactions (uId, type ,amount, category, description , source , date, paymentMethod) values( ?,  ?,  ?,  ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, t.getUserId());
            ps.setDouble(2, t.getAmount());
            ps.setInt(3, t.getCategoryId());
            ps.setDate(4, t.getDate());
            ps.setString(5, t.getType());
            ps.setString(6, t.getDescription());
            ps.setString(7, t.getSource());
            ps.setDate(8, t.getDate());
            ps.setString(9, t.getPaymentMethod());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Transaction save failed: " + e.getMessage());
        }
        return false;
    }

    public List<Transaction> fetchTransactions(int userId) {

        String command = "SELECT * FROM transactions WHERE uId = ? ORDER BY date DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);

            ps.executeQuery();

            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.getResultSet();

            // in new, it is better to just type <> instead of the type itself, it is called diamond operator, it is used to infer the type of the list from the context, it makes the code cleaner and less redundant
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
                //                 public Transaction(int transactionId, int userId, String type,
                //         double amount, int categoryId, String description,
                //         String date, String source, String paymentMethod) {
                // }
                Transaction t = new Transaction(
                        rs.getInt("tId"),
                        rs.getInt("uId"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getInt("categoryId"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getString("source"),
                        rs.getString("paymentMethod")
                );
                transactions.add(t);
            }
            return transactions;
        } catch (SQLException e) {
            System.err.println("Transactions fetch failed: " + e.getMessage());
        }
        return null;
    }

    public List<Transaction> fetchTransactionsByCategory(int uId, int category) {
        String command = "Select * from transactions where uId = ? AND cId= ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            // and she got composure
            ps.setInt(1, uId);
            ps.setInt(2, category);

            ps.executeQuery();

            ResultSet rs = ps.getResultSet();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("tId"),
                        rs.getInt("uId"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getInt("categoryId"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getString("source"),
                        rs.getString("paymentMethod")
                );
                transactions.add(t);
            }
            return transactions;
        } catch (SQLException e) {
            System.err.println("Transaction fetch failed: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteTransaction(int tId) {
        String command = "DELETE from transactions where tId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, tId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Transaction delete failed: " + e.getMessage());
        }
        return false;
    }

    //budget
    public boolean saveBudget(Budget b) {
        // budgets uId INTEGER, category TEXT,  limitAmount REAL, currentSpent REAL, startDate TEXT, endDate TEXT
        String command = "Insert into budgets (uId, category, limitAmount, currentSpent, startDate, endDate) values( ?,  ?,  ?,  ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getCategoryId());
            ps.setDouble(3, b.getLimitAmount());
            ps.setDouble(4, b.getCurrentSpent());
            ps.setString(5, b.getStartDate());
            ps.setString(6, b.getEndDate());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Budget save failed: " + e.getMessage());
        }
        return false;

    }

    public boolean updateBudget(Budget b) {
        String command = "UPDATE budgets SET limitAmount = ?, currentSpent = ?, startDate = ?, endDate = ? WHERE uId = ? AND category = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setDouble(1, b.getLimitAmount());
            ps.setDouble(2, b.getCurrentSpent());
            ps.setString(3, b.getStartDate());
            ps.setString(4, b.getEndDate());
            ps.setInt(5, b.getUserId());
            ps.setInt(6, b.getCategoryId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Budget update failed: " + e.getMessage());
        }
        return false;
    }

    public List<Budget> fetchBudgets(int userId) {

        String command = "Select * from budgets where uId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            List<Budget> budgets = new ArrayList<>();
            while (rs.next()) {
                Budget b = new Budget(
                        rs.getInt("uId"),
                        rs.getInt("cId"),
                        rs.getDouble("limitAmount"),
                        rs.getDouble("currentSpent"),
                        rs.getString("startDate"),
                        rs.getString("endDate")
                );
                budgets.add(b);
            }
            return budgets;
        } catch (SQLException e) {
            System.err.println("Budget fetch failed: " + e.getMessage());
        }
        return null;
    }

    //** category
    public boolean saveCategory(Category c) {
        String command = "Insert into categories (name, type) values( ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getType());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Category save failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCategory(Category c) {
        String command = "UPDATE categories SET name = ?, type = ? WHERE cId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getType());
            ps.setInt(3, c.getCategoryId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Category update failed: " + e.getMessage());
        }
        return false;
    }

    public List<Category> fetchCategories(int userId) {
        String command = "Select * from categories where isActive = 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("cId"),
                        rs.getString("name"),
                        rs.getString("type")
                );
                categories.add(c);
            }
            return categories;
        } catch (SQLException e) {
            System.err.println("Category fetch failed: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteCategory(int cId) {
        String command = "UPDATE categories SET isActive = 0 WHERE cId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, cId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Category delete failed: " + e.getMessage());
        }
        return false;
    }

    //** */ notification
    public boolean saveNotification(Notification n) {
        String command = "Insert into notifications (uId, message) values( ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getMessage());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Notification save failed: " + e.getMessage());
        }
        return false;

    }

    public List<Notification> fetchNotifications(int userId) {
        String command = "SELECT * FROM notifications WHERE uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Notification> notifications = new ArrayList<>();
            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("nId"),
                        rs.getInt("uId"),
                        rs.getString("message"),
                        rs.getBoolean("isRead"),
                        rs.getString("date")
                );
                notifications.add(n);
            }
            return notifications;
        } catch (SQLException e) {
            System.err.println("Notification fetch failed: " + e.getMessage());
        }
        return null;
    }

}