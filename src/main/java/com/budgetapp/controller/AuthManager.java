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

    private static AuthManager instance;
    private User currentUser;
    private DatabaseManager databaseManager;
    private String currentEmail;

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

    public void logout() {
        currentUser = null;
        currentEmail = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean updateProfile(String n, String c) {
        if (currentUser != null) {
            currentUser.setName(n);
            currentUser.setCurrency(c);
            databaseManager.updateUser(currentUser);
            return true;
        }
        return false;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

}
