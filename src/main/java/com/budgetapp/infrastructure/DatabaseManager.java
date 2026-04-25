package com.budgetapp.infrastructure;

// you can't acces subpackages from the parent package, they are just names
import com.budgetapp.model.*;

import java.util.List;
import java.sql.*;

public class DatabaseManager {

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
            "CREATE TABLE IF NOT EXISTS accounts (password TEXT NOT NULL, uId INTEGER PRIMARY KEY, FOREIGN KEY(uId) REFERENCES users(uId) on DELETE CASCADE );",
            // if source is null, then it is an expense, if paymentMethod is null, then it is an income
            "CREATE TABLE IF NOT EXISTS transactions (tId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, type TEXT CHECK(type IN ('income', 'expense')) NOT NULL, amount REAL NOT NULL, cId INTEGER,category TEXT, description TEXT, date TEXT DEFAULT (datetime('now')), source TEXT, paymentMethod TEXT, FOREIGN KEY(uId) REFERENCES users(uId), FOREIGN KEY(cId) REFERENCES categories(cId));",
            "CREATE TABLE IF NOT EXISTS budgets (bId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, category TEXT NOT NULL,  limitAmount REAL NOT NULL, currentSpent REAL DEFAULT 0, startDate TEXT, endDate TEXT, FOREIGN KEY(uId) REFERENCES users(uId), FOREIGN KEY(cId) REFERENCES categories(cId));",
            "CREATE TABLE IF NOT EXISTS categories (cId INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, type TEXT CHECK(type IN ('income', 'expense', 'both')) DEFAULT 'both', isActive INTEGER DEFAULT 1);",
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

    // here we start to use prepared statments, unlike normal statements
    // prepared statments are precompiled sql statement that run with different values each time
    // we use placeholder ? and set them by setString
    //** user
    public boolean saveUser(User u) {
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
    }

    public boolean updateUser(User u) {
        String command = "UPDATE users SET fullName = ?, email = ?, passwordHash = ?, currency = ?, language = ? WHERE uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getCurrency());
            ps.setString(5, u.getLanguage());
            ps.setInt(6, u.getUserId());

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
                User user = new User();
                // get returns the attributes of the current row
                user.setUserId(rs.getInt("uId"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("passwordHash"));
                user.setCurrency(rs.getString("currency"));
                user.setLanguage(rs.getString("language"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("User fetch failed: " + e.getMessage());
        }
        return null;
    }

    //** account
    public Account fetchAccount(int userId) {
        return null;
    }

    // ── Person 2 needs these ──
    //** transaction
    public boolean saveTransaction(Transaction t) {
        return false;
    }

    public List<Transaction> fetchTransactions(int userId) {
        return null;
    }

    public List<Transaction> fetchTransactionsByCatgegory(int uId, String category) {
        return null;
    }

    public boolean deleteTransaction(int tId) {
        return true;
    }

    //** budget
    public boolean saveBudget(Budget b) {
        return false;
    }

    public boolean updateBudget(Budget b) {
        return false;
    }

    public List<Budget> fetchBudgets(int userId) {
        return null;
    }

    //** */ category
    public boolean saveCategory(Category c) {
        return false;
    }

    //** */ notification
    public boolean saveNotification(Notification n) {
        return false;
    }

    public List<Notification> fetchNotifications(int userId) {
        return null;
    }

}
