package com.budgetapp.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Account;
import com.budgetapp.model.User;

/**
 * Manages all authentication operations including login, registration, and
 * session management.
 *
 * <p>
 * Implements the Singleton pattern to ensure only one AuthManager instance
 * exists throughout the application.</p>
 *
 * @version 1.0
 */
public class AuthManager {

    /**
     * The single instance of AuthManager for the application.
     */
    private static AuthManager instance;

    /**
     * The currently authenticated user's profile information.
     */
    private User currentUser;

    /**
     * Reference to the database manager for account and user persistence.
     */
    private DatabaseManager databaseManager;

    /**
     * The email address of the currently logged-in user.
     */
    private String currentEmail;

    /**
     * Hashing the password for protection using MD5.
     *
     * @param password the plain text password
     * @return the string of the Hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte a : bytes) {
                sb.append(Integer.toString((a & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    /**
     * Private constructor to prevent external instantiation. Initializes the
     * connection to the DatabaseManager.
     */
    private AuthManager() {
        databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Authenticates a user using their email and password. Password is hashed
     * using MD5 before comparison.
     *
     * @param email the user's registered email address
     * @param password the plain text password entered by the user
     * @return true if credentials are valid, false otherwise
     */
    public boolean login(String email, String password) {
        Account a = databaseManager.fetchAccountByEmail(email);
        if (a != null) {
            String hashed = hashPassword(password);
            if (a.getPassword().equals(hashed)) {
                currentUser = databaseManager.fetchUser(a.getUserId());
                currentEmail = email;
                return true;
            }
        }
        return false;
    }

    /**
     * Registers a new user in the system. Checks for duplicate emails before
     * creating the account.
     *
     * @param name the full name of the new user
     * @param email the email address (must be unique)
     * @param password the plain text password (will be hashed)
     * @param currency the preferred display currency
     * @return true if registration succeeded, false if email exists
     */
    public boolean register(String name, String email, String password, String currency) {
        if (databaseManager.fetchAccountByEmail(email) != null) {
            return false;
        }
        String hashed = hashPassword(password);
        User newUser = new User(0, name, 0.0, currency);
        int id = databaseManager.saveUser(newUser);
        newUser.setId(id);
        Account newAccount = new Account(email, hashed, newUser.getId());
        databaseManager.saveAccount(newAccount);
        return true;

    }

    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        currentUser = null;
        currentEmail = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the current {@link User} object, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Updates the current user's name and preferred currency.
     *
     * @param name the new name for the user
     * @param currency the new currency code
     * @return true if the profile was successfully updated, false if no user is
     * logged in
     */
    public boolean updateProfile(String name, String currency) {
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setCurrency(currency);
            databaseManager.updateUser(currentUser);
            return true;
        }
        return false;
    }

    /**
     * Returns the email address of the session user.
     *
     * @return the current user's email as a String, or null if not logged in
     */
    public String getCurrentEmail() {
        return currentEmail;
    }

    /**
     * Returns the singleton instance of AuthManager. Creates the instance if it
     * does not exist yet.
     *
     * @return the single AuthManager instance
     */
    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

}
