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
    private String hashPassword(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(p.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte a : bytes) {
                sb.append(Integer.toString((a & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return p;
        }
    }

    /**
     * Private constructor to prevent external instantiation. Initializes the c
     * nnection to the DatabaseManager.
     */
    public AuthManager() {
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
    public boolean login(String e, String p) {
        Account a = databaseManager.fetchAccountByEmail(e);
        if (a != null) {
            String hashed = hashPassword(p);
            if (a.getPassword().equals(hashed)) {
                currentUser = databaseManager.fetchUser(a.getUserId());
                currentEmail = e;
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
    public boolean register(String n, String e, String p, String c) {
        if (databaseManager.fetchAccountByEmail(e) != null) {
            return false;
        }
        String hashed = hashPassword(p);
        User newUser = new User(0, n, 0.0, c);
        int id = databaseManager.saveUser(newUser);
        newUser.setId(id);
        Account newAccount = new Account(e, hashed, newUser.getId());
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
     * @param n the new name for the user
     * @param c the new currency code
     * @return true if the profile was successfully updated, false if no user is
     * logged in
     */
    public boolean updateProfile(String n, String c) {
        if (currentUser != null) {
            currentUser.setName(n);
            currentUser.setCurrency(c);
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
