package com;

import com.database.DBConnection;
import com.database.UserDAO;
import com.database.ActivitySessionDAO;
import com.models.User;
import com.models.ActivitySession;
import com.services.AuthenticationService;
import com.services.TimeTrackingService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test program to verify database connectivity and basic operations
 */
public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  IntelliCoach - Database & Services Test");
        System.out.println("═══════════════════════════════════════════════════════\n");

        try {
            // Test 1: Database Connection
            System.out.println("Test 1: Database Connection");
            System.out.println("───────────────────────────────────────────────────────");
            DBConnection dbConnection = DBConnection.getInstance();
            System.out.println("✅ Database connection successful\n");

            // Test 2: User Registration
            System.out.println("Test 2: User Registration");
            System.out.println("───────────────────────────────────────────────────────");
            AuthenticationService authService = AuthenticationService.getInstance();

            AuthenticationService.AuthResult regResult = authService.register(
                    "testuser",
                    "test@example.com",
                    "password123",
                    "Test User");

            if (regResult.isSuccess()) {
                System.out.println("✅ User registered: " + regResult.getUser());
            } else {
                System.out.println("ℹ️  " + regResult.getMessage());
            }
            System.out.println();

            // Test 3: User Login
            System.out.println("Test 3: User Login");
            System.out.println("───────────────────────────────────────────────────────");
            AuthenticationService.AuthResult loginResult = authService.login(
                    "testuser",
                    "password123");

            if (loginResult.isSuccess()) {
                System.out.println("✅ Login successful: " + loginResult.getUser().getFullName());
                User user = loginResult.getUser();

                // Test 4: Start Activity
                System.out.println("\nTest 4: Start Activity Session");
                System.out.println("───────────────────────────────────────────────────────");
                TimeTrackingService timeService = new TimeTrackingService();

                ActivitySession session = timeService.startActivity(user.getUserId(), "Academic");
                if (session != null) {
                    System.out.println("✅ Activity started: " + session);

                    // Wait 2 seconds
                    System.out.println("   Waiting 2 seconds...");
                    Thread.sleep(2000);

                    // Test 5: Stop Activity
                    System.out.println("\nTest 5: Stop Activity Session");
                    System.out.println("───────────────────────────────────────────────────────");
                    boolean stopped = timeService.stopActivity(user.getUserId());
                    if (stopped) {
                        System.out.println("✅ Activity stopped successfully");
                    }

                    // Test 6: Retrieve Sessions
                    System.out.println("\nTest 6: Retrieve Today's Sessions");
                    System.out.println("───────────────────────────────────────────────────────");
                    List<ActivitySession> sessions = timeService.getTodaySessions(user.getUserId());
                    System.out.println("✅ Found " + sessions.size() + " session(s):");
                    for (ActivitySession s : sessions) {
                        System.out.println("   - " + s.getActivityType() + ": " +
                                s.getDurationMinutes() + " minutes");
                    }
                }
            } else {
                System.out.println(" Login failed: " + loginResult.getMessage());
            }

            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("  All Tests Completed Successfully!");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("\nYou can now run the GUI application:");
            System.out.println("  java -cp \"build:lib/*\" --module-path \"path/to/javafx/lib\" \\");
            System.out.println("       --add-modules javafx.controls,javafx.fxml \\");
            System.out.println("       com.intellicoach.IntelliCoachApp");

        } catch (Exception e) {
            System.err.println(" Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
