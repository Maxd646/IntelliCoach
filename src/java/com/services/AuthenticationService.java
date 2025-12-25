package com.services;

import com.models.User;
import com.database.UserDAO;
import com.abstracts.BaseService;

public class AuthenticationService extends BaseService {
    private static AuthenticationService instance;
    private UserDAO userDAO;
    private User currentUser;

    private AuthenticationService() {
        super("Authentication");
        this.userDAO = new UserDAO();
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Register a new user
     */
    public AuthResult register(String username, String email, String password, String fullName) {
        // Validation
        if (username == null || username.trim().length() < 3) {
            return new AuthResult(false, "Username must be at least 3 characters");
        }

        if (email == null || !email.contains("@")) {
            return new AuthResult(false, "Invalid email format");
        }

        if (password == null || password.length() < 6) {
            return new AuthResult(false, "Password must be at least 6 characters");
        }

        // Check if username exists
        if (userDAO.findByUsername(username) != null) {
            return new AuthResult(false, "Username already exists");
        }

        // Check if email exists
        if (userDAO.findByEmail(email) != null) {
            return new AuthResult(false, "Email already registered");
        }

        // Create user (password encoding would go here in production)
        String encodedPassword = encodePassword(password);
        User user = new User(username, email, encodedPassword, fullName);
        User createdUser = userDAO.save(user);

        if (createdUser != null) {
            return new AuthResult(true, "Registration successful", createdUser);
        } else {
            return new AuthResult(false, "Registration failed");
        }
    }

    /**
     * Login user
     */
    public AuthResult login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return new AuthResult(false, "Username and password required");
        }

        // Try to find user by username or email
        User user = userDAO.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userDAO.findByEmail(usernameOrEmail);
        }
        if (user == null) {
            return new AuthResult(false, "User not found please create account before");
        }

        // Verify password
        String encodedPassword = encodePassword(password);
        if (!user.getPassword().equals(encodedPassword)) {
            return new AuthResult(false, "Invalid password");
        }

        // Set current user
        this.currentUser = user;
        return new AuthResult(true, "Login successful", user);
    }

    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Simple password encoding
     */
    private String encodePassword(String password) {
        return password + "_encoded";
    }

    /**
     * Authentication result class
     */
    public static class AuthResult {
        private boolean success;
        private String message;
        private User user;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }

    // Abstract method implementations from BaseService
    @Override
    protected void doInitialize() throws Exception {
        // Initialize user DAO and verify database connection
        if (userDAO == null) {
            throw new Exception("UserDAO not initialized");
        }
        // Test database connection
        userDAO.count();
    }

    @Override
    protected void doCleanup() {
        // Logout current user and cleanup resources
        if (currentUser != null) {
            logout();
        }
    }
}
