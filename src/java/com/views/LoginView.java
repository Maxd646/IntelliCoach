package com.views;

import com.services.AuthenticationService;
import com.services.AuthenticationService.AuthResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private Stage stage;
    private AuthenticationService authService;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.authService = AuthenticationService.getInstance();
    }

    public Scene createScene() {
        // Main container with colorful layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("login-background-colorful");

        // Left side - Welcome section with colors
        VBox leftPanel = new VBox(30);
        leftPanel.setPadding(new Insets(60));
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPrefWidth(500);
        leftPanel.getStyleClass().add("welcome-panel");

        Label welcomeLabel = new Label("Welcome to");
        welcomeLabel.getStyleClass().add("welcome-large");

        Label titleLabel = new Label("IntelliCoach");
        titleLabel.getStyleClass().add("brand-title");

        Label subtitleLabel = new Label(" Boost Your Productivity");
        subtitleLabel.getStyleClass().add("brand-subtitle");

        Label descriptionLabel = new Label(
                "Track your time, analyze your habits, and achieve your goals with our intelligent coaching system.");
        descriptionLabel.getStyleClass().add("brand-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setPrefWidth(400);

        // Feature highlights with colors
        VBox featuresBox = new VBox(15);
        featuresBox.getStyleClass().add("features-box");

        Label feature1 = new Label(" Real-time Activity Tracking");
        feature1.getStyleClass().add("feature-item");

        Label feature2 = new Label(" Smart Analytics Dashboard");
        feature2.getStyleClass().add("feature-item");

        Label feature3 = new Label(" Personalized Recommendations");
        feature3.getStyleClass().add("feature-item");

        featuresBox.getChildren().addAll(feature1, feature2, feature3);

        leftPanel.getChildren().addAll(welcomeLabel, titleLabel, subtitleLabel, descriptionLabel, featuresBox);

        // Right side - Login form in top-right corner
        VBox rightPanel = new VBox();
        rightPanel.setPadding(new Insets(40));
        rightPanel.setPrefWidth(400);

        // Login card positioned at top-right
        VBox loginCard = new VBox(25);
        loginCard.setAlignment(Pos.TOP_CENTER);
        loginCard.setPadding(new Insets(40));
        loginCard.getStyleClass().add("login-card-colorful");
        loginCard.setMaxWidth(350);

        // Login header
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label loginTitle = new Label("Sign In");
        loginTitle.getStyleClass().add("login-card-title");

        Label loginSubtitle = new Label("Access your dashboard");
        loginSubtitle.getStyleClass().add("login-card-subtitle");

        headerBox.getChildren().addAll(loginTitle, loginSubtitle);

        // Form container
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);

        // Username field with colorful styling
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Username or Email");
        usernameLabel.getStyleClass().add("form-label-colorful");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username or email");
        usernameField.setPrefWidth(280);
        usernameField.setPrefHeight(45);
        usernameField.getStyleClass().add("colorful-textfield");

        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field with colorful styling
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("form-label-colorful");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(280);
        passwordField.setPrefHeight(45);
        passwordField.getStyleClass().add("colorful-textfield");

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        formBox.getChildren().addAll(usernameBox, passwordBox);

        // Message label
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(280);
        messageLabel.setAlignment(Pos.CENTER);

        // Buttons with colorful styling
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(280);
        loginButton.setPrefHeight(50);
        loginButton.getStyleClass().addAll("colorful-btn", "btn-login");

        // Register link section
        HBox registerBox = new HBox(5);
        registerBox.setAlignment(Pos.CENTER);

        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.getStyleClass().add("link-text-colorful");

        Button registerButton = new Button("Create Account");
        registerButton.getStyleClass().add("link-button-colorful");

        registerBox.getChildren().addAll(noAccountLabel, registerButton);

        buttonBox.getChildren().addAll(loginButton, registerBox);

        loginCard.getChildren().addAll(headerBox, formBox, messageLabel, buttonBox);

        // Add login card to top of right panel
        rightPanel.getChildren().add(loginCard);
        VBox.setVgrow(loginCard, Priority.NEVER);

        // Set panels
        root.setLeft(leftPanel);
        root.setRight(rightPanel);

        // Footer with colors
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));
        footer.getStyleClass().add("colorful-footer");

        Label footerLabel = new Label("© 2024 IntelliCoach - Empowering Productivity Worldwide ");
        footerLabel.getStyleClass().add("colorful-footer-text");

        footer.getChildren().add(footerLabel);
        root.setBottom(footer);

        // Event handlers using lambda expressions
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showMessage(messageLabel, "Please enter username and password", true);
                return;
            }
            AuthResult result = authService.login(username, password);

            if (result.isSuccess()) {
                showMessage(messageLabel, "Login successful!", false);
                // Navigate to dashboard
                DashboardView dashboardView = new DashboardView(stage, result.getUser());
                stage.setScene(dashboardView.createScene());
            } else {
                showMessage(messageLabel, result.getMessage(), true);
            }
        });

        registerButton.setOnAction(e -> {
            RegistrationView registrationView = new RegistrationView(stage);
            stage.setScene(registrationView.createScene());
        });

        // Enter key triggers login
        passwordField.setOnAction(e -> loginButton.fire());

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }

    private void showMessage(Label label, String message, boolean isError) {
        label.setText(message);
        label.getStyleClass().removeAll("message-success", "message-error");
        label.getStyleClass().add(isError ? "message-error" : "message-success");
    }
}
