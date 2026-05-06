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
import com.budgetapp.model.Expense;
import com.budgetapp.model.Income;
import com.budgetapp.model.Notification;
import com.budgetapp.model.Transaction;
import com.budgetapp.model.User;

// dealing with sums and filtering is always better in the db
/**
 * Singleton class responsible for all SQLite database operations. Handles
 * initialization of tables and all CRUD operations for users, transactions,
 * budgets, and notifications.
 *
 * @version 1.0
 */
public class DatabaseManager {

    // to be or not to be, this is the biggest question
    // make it .db to view it
    private static final String URL = "jdbc:sqlite:TheBrokeDiariesDatabase.db";

    // for it to be singleton, it has to has one instance only;
    private static DatabaseManager instance;

    // the constructor would be private for no one to make a new database
    private DatabaseManager() {
    }

    // public static DatabaseManager getInstance() {
    //     if (instance == null) {
    //         instance = new DatabaseManager();
    //     }
    //     return instance;
    // }
    // why use get instance
    // we often use it to make sure only one instance is constant through the whole program
    // that is why we only make a new databasemanager if there haven't been one
    // we also can use it when making a new instance is very complicated
    // so we hide all the comlixe logic behind one function call
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Establishes a connection to the SQLite database file.
     *
     * @return a {@link Connection} object to the local database
     * @throws SQLException if the connection cannot be established
     */
    private static Connection getConnection() throws SQLException {
        // the driver manager is the java sql part that handles connecting to an existing database
        return DriverManager.getConnection(URL);
    }

    /**
     * populates the database with initial demo data (users, accounts, and
     * transactions) if the users table is currently empty.
     */
    private void seedDatabase() {
        // Check if we already have users to avoid duplicate seeding
        String checkSql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }

            // 1. Insert a Default User
            // Note: Password 'pass123' hashed with MD5
            String userSql = "INSERT INTO users (fullName, currency, balance) VALUES ('John Doe', 'GBP', 1250.00)";
            stmt.executeUpdate(userSql);

            // 2. Insert Account for the User (linked to uId 1)
            String accountSql = "INSERT INTO accounts (email, password, uId) VALUES ('test@broke.com', '32250170a0dca92d53ec9624f336ca24', 1)";
            stmt.executeUpdate(accountSql);

            // 3. Insert Categories
            insertDefaultCategories();

            // 4. Insert Initial Transactions
          String transSql = "INSERT INTO transactions (uId, amount, type, cId, description, date) VALUES "
                    + "(1, 2000.00, 'income', 8, 'Monthly Pay', '2023-10-01'), "    
                    + "(1, 800.00, 'expense', 3, 'October Rent', '2023-10-02'), "  
                    + "(1, 50.00, 'expense', 1, 'Pizza Night', '2023-10-05')";
            stmt.executeUpdate(transSql);

            System.out.println("Database successfully seeded with demo data.");

        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }

    /**
     * Creates all necessary database tables if they do not already exist.
     * Enables foreign key constraints and triggers the initial data seeding.
     */
    public void initializeDatabase() {

        String pragmaSql = "PRAGMA foreign_keys = ON;";
        String[] sqlQueries = {
            //! I MUST DISCUSSE THE CATEGORY
            "CREATE TABLE IF NOT EXISTS users (uId INTEGER PRIMARY KEY AUTOINCREMENT, fullName TEXT NOT NULL, currency TEXT DEFAULT 'Pound',balance REAL DEFAULT 0);",
            // why a seperate table for accouts?
            // "CREATE TABLE IF NOT EXISTS accounts (aId INTEGER PRIMARY KEY AUTOINCREMENT, password TEXT NOT NULL, uId INTEGER UNIQUE NOT NULL, FOREIGN KEY(uId) REFERENCES users(uId) on DELETE CASCADE );",
            "CREATE TABLE IF NOT EXISTS accounts (password TEXT NOT NULL, uId INTEGER PRIMARY KEY, email TEXT UNIQUE NOT NULL, FOREIGN KEY(uId) REFERENCES users(uId) on DELETE CASCADE );",
            // if source is null, then it is an expense, if paymentMethod is null, then it is an income
            "CREATE TABLE IF NOT EXISTS transactions (tId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, type TEXT CHECK(type IN ('income', 'expense')) NOT NULL, amount REAL NOT NULL, cId INTEGER, description TEXT, date DATE DEFAULT (datetime('now')), source TEXT, paymentMethod TEXT, FOREIGN KEY(uId) REFERENCES users(uId), FOREIGN KEY(cId) REFERENCES categories(cId));",
            "CREATE TABLE IF NOT EXISTS budgets (bId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, cId INTEGER NOT NULL, limitAmount REAL NOT NULL, currentSpent REAL DEFAULT 0, startDate DATE DEFAULT (datetime('now')), endDate DATE, FOREIGN KEY(uId) REFERENCES users(uId), FOREIGN KEY(cId) REFERENCES categories(cId));",
            "CREATE TABLE IF NOT EXISTS categories (cId INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, isActive BOOLEAN DEFAULT 1);",
            "CREATE TABLE IF NOT EXISTS notifications (nId INTEGER PRIMARY KEY AUTOINCREMENT, uId INTEGER NOT NULL, message TEXT NOT NULL, isRead INTEGER DEFAULT 0, date DATE DEFAULT (datetime('now')), FOREIGN KEY(uId) REFERENCES users(uId));"};

        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute(pragmaSql);
            for (String oneQuery : sqlQueries) {
                st.execute(oneQuery);
            }
        } catch (SQLException e) {
            System.err.println("Setup failed: " + e.getMessage());
        }

        seedDatabase();
    }

    public void insertDefaultCategories() {
        String sql = "INSERT OR IGNORE INTO categories (name) VALUES (?)";

        String[] defaults = {
            // expense categories
            "Food & Dining",
            "Transport",
            "Bills & Utilities",
            "Entertainment",
            "Shopping",
            "Health",
            "Education",
            // income categories
            "Salary",
            "Freelance",
            "Gift",
            // both
            "Other"
        };

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            for (String cat : defaults) {
                ps.setString(1, cat);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("insertDefaultCategories failed: " + e.getMessage());
        }
    }

    // here we start to use prepared statements, unlike normal statements
    // prepared statements are precompiled sql statement that run with different values each time
    // we use placeholder ? and set them by setString
    // saving a user in register
    //** user
    // returns the generated user id, or -1 if failed
    /**
     * Persists a new User profile to the database.
     *
     * @param u the {@link User} object to save
     * @return the auto-generated uId from the database, or -1 if the operation
     * fails
     */
    public int saveUser(User u) {
        String command = "Insert into users (fullName, currency, balance) values( ?,  ?,  ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getCurrency());
            ps.setDouble(3, u.getBalance());

            // we use executeUpdate for insert, update and delete statements, it returns the number of affected rows
            ps.executeUpdate();

            // getGeneratedKeys returns a ResultSet containing the auto-generated keys
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("User save failed: " + e.getMessage());
        }

        return -1;
    }

    public boolean updateUser(User u) {
        String command = "UPDATE users SET fullName = ?, balance = ?, currency = ? WHERE uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, u.getName());
            ps.setDouble(2, u.getBalance());
            ps.setString(3, u.getCurrency());
            ps.setInt(4, u.getId());

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

            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("uId"),
                        rs.getString("fullName"),
                        rs.getDouble("balance"),
                        rs.getString("currency")
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
    public Account fetchAccountByUserId(int userId) {

        String command = "SELECT * FROM accounts WHERE uId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);

            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = new Account(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("uId"));
                // get returns the attributes of the current row
                return account;
            }

        } catch (SQLException e) {
            System.err.println("Account fetch failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user's account credentials based on their unique email
     * address.
     *
     * @param email the email to search for
     * @return the {@link Account} object if found, otherwise null
     */
    public Account fetchAccountByEmail(String email) {
        String command = "SELECT * FROM accounts WHERE email = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("uId"));
                return account;
            }

        } catch (SQLException e) {
            System.err.println("Account fetch failed: " + e.getMessage());
        }
        return null;
    }

    public boolean saveAccount(Account a) {
        String command = "Insert into accounts (password, uId, email) values(?,?,?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, a.getPassword());
            ps.setInt(2, a.getUserId());
            ps.setString(3, a.getEmail());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Account save failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAccount(Account a) {
        String command = "UPDATE accounts SET email = ?, password = ? WHERE uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, a.getEmail());
            ps.setString(2, a.getPassword());
            ps.setInt(3, a.getUserId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Account update failed: " + e.getMessage());
        }
        return false;
    }

    //** transaction
    // you can save an income or an expense using the same method, because they are both transactions, and they have the same attributes, except for the source and paymentMethod, which can be null for one of them
    /**
     * Saves a transaction (either Income or Expense) to the database. Uses
     * 'instanceof' to determine which specific fields (source vs paymentMethod)
     * to populate.
     *
     * @param t the {@link Transaction} object (Income or Expense) to persist
     * @return true if saved successfully, false otherwise
     */
    public boolean saveTransaction(Transaction t) {
        String command = "Insert into transactions (uId, type ,amount, cId, description ,date, source ,  paymentMethod) values( ?,  ?,  ?,  ?, ?, ?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {

            //   source , date, paymentMethod) 
            ps.setInt(1, t.getUserId());
            ps.setString(2, t instanceof Income ? "income" : "expense");
            ps.setDouble(3, t.getAmount());
            ps.setInt(4, t instanceof Income ? ((Income) t).getCategoryId() : ((Expense) t).getCategoryId());
            ps.setString(5, t.getDescription());
            ps.setDate(6, new java.sql.Date(t.getDate().getTime()));
            ps.setString(7, t.getType().equals("income") ? t.getExtra() : null);
            ps.setString(8, t.getType().equals("expense") ? t.getExtra() : null);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Transaction save failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateTransaction(Transaction t) {
        String command = "UPDATE transactions SET amount = ?, cId = ?, description = ?, date = ?, source = ?, paymentMethod = ? WHERE tId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setDouble(1, t.getAmount());
            ps.setInt(2, t instanceof Income ? ((Income) t).getCategoryId() : ((Expense) t).getCategoryId());
            ps.setString(3, t.getDescription());
            ps.setDate(4, new java.sql.Date(t.getDate().getTime()));
            ps.setString(5, t instanceof Income ? ((Income) t).getSource() : null);
            ps.setString(6, t instanceof Income ? null : ((Expense) t).getPaymentMethod());
            ps.setInt(7, t.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Transaction update failed: " + e.getMessage());
        }
        return false;
    }

    public Transaction fetchTransaction(int tId) {
        String command = "SELECT * FROM transactions WHERE tId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, tId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Transaction t;
                if (rs.getString("type").equals("income")) {
                    t = new Income(
                            rs.getInt("tId"),
                            rs.getInt("uId"),
                            rs.getInt("cId"),
                            rs.getDouble("amount"),
                            rs.getDate("date"),
                            rs.getString("description"),
                            rs.getString("source")
                    );
                } else {
                    t = new Expense(
                            rs.getInt("tId"),
                            rs.getInt("uId"),
                            rs.getInt("cId"),
                            rs.getDouble("amount"),
                            rs.getDate("date"),
                            rs.getString("description"),
                            rs.getString("paymentMethod")
                    );
                }
                return t;
            }

        } catch (SQLException e) {
            System.err.println("Transaction fetch failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all transactions for a specific user, ordered by date
     * descending.
     *
     * @param userId the ID of the user whose transactions are being fetched
     * @return a List of {@link Transaction} objects
     */
    public List<Transaction> fetchTransactions(int userId) {

        String command = "SELECT * FROM transactions WHERE uId = ? ORDER BY date DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);

            // ps.executeQuery();
            // we use executeQuery for select statements, it returns a ResultSet object that contains the data returned by the query
            // we can use the ResultSet object to iterate through the rows of the result and get the values of the columns by using the getString, getInt, etc. methods
            // for example, if we want to get the fullName column value of the first row, we can use rs.getString("fullName") or rs.getString(2) if fullName is the second column in the select statement
            // we can also check if there are more rows by using rs.next() method, which returns true if there is a next row and moves the cursor to that row, or false if there are no more rows
            // in this case, we expect only one row to be returned, so we can just check if rs.next() is true and then get the values of the columns to create a User object and return it
            // if rs.next() is false, it means there is no user with the given userId, so we can return null
            ResultSet rs = ps.executeQuery();

            // in new, it is better to just type <> instead of the type itself, it is called diamond operator, it is used to infer the type of the list from the context, it makes the code cleaner and less redundant
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
                //                 public Transaction(int transactionId, int userId, String type,
                //         double amount, int categoryId, String description,
                //         String date, String source, String paymentMethod) {
                // }

                Transaction t;
                if (rs.getString("type").equals("income")) {
                    t = new Income(
                            rs.getInt("tId"),
                            rs.getInt("uId"),
                            rs.getInt("cId"),
                            rs.getDouble("amount"),
                            rs.getDate("date"),
                            rs.getString("description"),
                            rs.getString("source")
                    );
                } else {
                    t = new Expense(
                            rs.getInt("tId"),
                            rs.getInt("uId"),
                            rs.getInt("cId"),
                            rs.getDouble("amount"),
                            rs.getDate("date"),
                            rs.getString("description"),
                            rs.getString("paymentMethod")
                    );
                }
                transactions.add(t);
            }
            return transactions;
        } catch (SQLException e) {
            System.err.println("Transactions fetch failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Calculates the sum of all income and expenses within a specific date
     * range.
     *
     * @param startDate the beginning of the interval
     * @param endDate the end of the interval
     * @return a List where index 0 is Total Income and index 1 is Total Expense
     */
    public List<Transaction> fetchTransactionsByCategory(int uId, int category) {
        String command = "Select * from transactions where uId = ? AND cId= ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            // and she got composure
            ps.setInt(1, uId);
            ps.setInt(2, category);

            // ps.executeQuery();
            // ResultSet rs = ps.getResultSet();
            ResultSet rs = ps.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction t;
                if (rs.getString("type").equals("income")) {
                    t = new Income(
                            rs.getInt("tId"),
                            rs.getInt("uId"),
                            rs.getInt("cId"),
                            rs.getDouble("amount"),
                            rs.getDate("date"),
                            rs.getString("description"),
                            rs.getString("source")
                    );
                } else {
                    t = new Expense(
                            rs.getInt("tId"),
                            rs.getInt("uId"),
                            rs.getInt("cId"),
                            rs.getDouble("amount"),
                            rs.getDate("date"),
                            rs.getString("description"),
                            rs.getString("paymentMethod")
                    );
                }
                transactions.add(t);
            }
            return transactions;
        } catch (SQLException e) {
            System.err.println("Transaction fetch failed: " + e.getMessage());
        }
        return null;
    }

    // returns the total income, expense, between a certain time interval
    public List<Double> fetchTotalIncomeExpenseBetween(int userId, java.util.Date startDate, java.util.Date endDate) {
        String command = "SELECT type, SUM(amount) as total FROM transactions WHERE uId=? AND date BETWEEN ? AND ? GROUP BY type";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);
            ps.setDate(2, new java.sql.Date(startDate.getTime()));
            ps.setDate(3, new java.sql.Date(endDate.getTime()));

            ResultSet rs = ps.executeQuery();

            double totalIncome = 0, totalExpense = 0;

            while (rs.next()) {
                if (rs.getString("type").equals("income")) {
                    totalIncome = rs.getDouble("total");
                } else {
                    totalExpense = rs.getDouble("total");
                }
            }
            return List.of(totalIncome, totalExpense);
        } catch (SQLException e) {
            System.err.println("Total income/expense fetch failed: " + e.getMessage());
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
        String command = "Insert into budgets (uId, cId, limitAmount, currentSpent, startDate, endDate) values( ?,  ?,  ?,  ?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getCategoryId());
            ps.setDouble(3, b.getLimit());
            ps.setDouble(4, b.getCurrentSpent());
            ps.setDate(5, new java.sql.Date(b.getStartDate().getTime()));
            ps.setDate(6, new java.sql.Date(b.getEndDate().getTime()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Budget save failed: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteBudgetsByUserId(int userId) {
        String command = "DELETE from budgets where uId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Budgets delete failed: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteBudget(int bId) {
        String command = "DELETE from budgets where bId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, bId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Budget delete failed: " + e.getMessage());
        }
        return false;
    }

    public Budget fetchBudget(int bId) {
        String command = "SELECT * FROM budgets WHERE bId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, bId);

            // ps.executeQuery();
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Budget b = new Budget(
                        rs.getInt("bId"),
                        rs.getInt("uId"),
                        rs.getInt("cId"),
                        rs.getDouble("limitAmount"),
                        rs.getDouble("currentSpent"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate")
                );
                return b;
            }

        } catch (SQLException e) {
            System.err.println("Budget fetch failed: " + e.getMessage());
        }
        return null;
    }

    //! wrong, we should update by budget id, not by user id and category, because a user can have multiple budgets for the same category but different time periods
    public boolean updateBudget(Budget b) {
        String command = "UPDATE budgets SET limitAmount = ?, currentSpent = ?, startDate = ?, endDate = ? WHERE bId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setDouble(1, b.getLimit());
            ps.setDouble(2, b.getCurrentSpent());
            ps.setDate(3, new java.sql.Date(b.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(b.getEndDate().getTime()));
            ps.setInt(5, b.getBudgetId());
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
            // ps.executeQuery();
            ResultSet rs = ps.executeQuery();

            List<Budget> budgets = new ArrayList<>();
            while (rs.next()) {
                Budget b = new Budget(
                        rs.getInt("bId"),
                        rs.getInt("uId"),
                        rs.getInt("cId"),
                        rs.getDouble("limitAmount"),
                        rs.getDouble("currentSpent"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate")
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
        String command = "Insert into categories (name) values(?);";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, c.getName());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Category save failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCategory(Category c) {
        String command = "UPDATE categories SET name = ?, isActive = ? WHERE cId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setString(1, c.getName());
            ps.setBoolean(2, c.isActive());
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
            // ps.executeQuery();
            ResultSet rs = ps.executeQuery();

            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("cId"),
                        rs.getString("name"),
                        rs.getBoolean("isActive")
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
        String command = "DELETE FROM categories WHERE cId = ?";
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

    public boolean markNotificationRead(int notificationId) {
        String command = "UPDATE notifications SET isRead = 1 WHERE nId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(command)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Mark notification read failed: " + e.getMessage());
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
