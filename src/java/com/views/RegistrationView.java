package com.views;

import com.services.AuthenticationService;
import com.services.AuthenticationService.AuthResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Registration View - VBox layout (Chapter 7 compliant)
 */
public class RegistrationView {
    private Stage stage;
    private AuthenticationService authService;

    public RegistrationView(Stage stage) {
        this.stage = stage;
        this.authService = AuthenticationService.getInstance();
    }

    public Scene createScene() {
        // Main container with colorful layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("register-background-colorful");

        // Center card container - smaller and positioned higher
        // Center card container
        VBox centerCard = new VBox(15);
        centerCard.setAlignment(Pos.TOP_CENTER);
        centerCard.setPadding(new Insets(25));
        centerCard.getStyleClass().add("register-card-colorful");
        centerCard.setMaxWidth(480);

        // Wrap center card in ScrollPane with proper centering
        StackPane centerContainer = new StackPane();
        centerContainer.getChildren().add(centerCard);
        centerContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(centerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false); // prevents stretching vertically
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background:transparent; -fx-background-color:transparent;");

        // Add to BorderPane
        root.setCenter(scrollPane);

        // Header section
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Join the IntelliCoach Family! ");
        welcomeLabel.getStyleClass().add("register-welcome");

        Label titleLabel = new Label("Create Your Account");
        titleLabel.getStyleClass().add("register-title");

        Label subtitleLabel = new Label(" Start your productivity journey today and unlock your potential!");
        subtitleLabel.getStyleClass().add("register-subtitle");
        subtitleLabel.setWrapText(true);

        headerBox.getChildren().addAll(welcomeLabel, titleLabel, subtitleLabel);

        // Form fields with compact styling
        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.CENTER);

        // Full Name with colorful styling
        VBox fullNameBox = new VBox(8);
        Label fullNameLabel = new Label(" Full Name");
        fullNameLabel.getStyleClass().add("register-form-label");
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Enter your full name");
        fullNameField.setPrefWidth(400);
        fullNameField.setPrefHeight(40);
        fullNameField.getStyleClass().add("register-textfield");
        fullNameBox.getChildren().addAll(fullNameLabel, fullNameField);

        // Username with colorful styling
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label(" Username");
        usernameLabel.getStyleClass().add("register-form-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a unique username (min 3 characters)");
        usernameField.setPrefWidth(400);
        usernameField.setPrefHeight(40);
        usernameField.getStyleClass().add("register-textfield");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Email with colorful styling
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label(" Email Address");
        emailLabel.getStyleClass().add("register-form-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setPrefWidth(400);
        emailField.setPrefHeight(40);
        emailField.getStyleClass().add("register-textfield");
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Password with colorful styling
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label(" Password");
        passwordLabel.getStyleClass().add("register-form-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a strong password (min 6 characters)");
        passwordField.setPrefWidth(400);
        passwordField.setPrefHeight(40);
        passwordField.getStyleClass().add("register-textfield");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Confirm Password with colorful styling
        VBox confirmPasswordBox = new VBox(8);
        Label confirmPasswordLabel = new Label(" Confirm Password");
        confirmPasswordLabel.getStyleClass().add("register-form-label");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter your password");
        confirmPasswordField.setPrefWidth(400);
        confirmPasswordField.setPrefHeight(40);
        confirmPasswordField.getStyleClass().add("register-textfield");
        confirmPasswordBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);

        formBox.getChildren().addAll(fullNameBox, usernameBox, emailBox, passwordBox, confirmPasswordBox);

        // Message label with compact styling
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(400);
        messageLabel.setAlignment(Pos.CENTER);

        // Buttons with compact styling
        VBox buttonBox = new VBox(12);
        buttonBox.setAlignment(Pos.CENTER);

        // Prominent Register Button - smaller
        Button registerButton = new Button(" Register");
        registerButton.setPrefWidth(400);
        registerButton.setPrefHeight(45);
        registerButton.getStyleClass().addAll("register-btn-main");

        // Back to login link with better styling
        HBox loginBox = new HBox(8);
        loginBox.setAlignment(Pos.CENTER);

        Label hasAccountLabel = new Label("Already have an account?");
        hasAccountLabel.getStyleClass().add("register-link-text");

        Button backButton = new Button("Sign in");
        backButton.getStyleClass().add("register-link-button");

        loginBox.getChildren().addAll(hasAccountLabel, backButton);

        buttonBox.getChildren().addAll(registerButton, loginBox);

        centerCard.getChildren().addAll(headerBox, formBox, messageLabel, buttonBox);

        // Center the card
        BorderPane.setAlignment(scrollPane, Pos.CENTER);

        // Footer with colorful styling
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(25));
        footer.getStyleClass().add("register-footer-colorful");

        Label footerLabel = new Label("© 2025 IntelliCoach - Join thousands of productive users worldwide!");
        footerLabel.getStyleClass().add("register-footer-text");

        footer.getChildren().add(footerLabel);
        root.setBottom(footer);

        // Event handlers using lambda expressions
        registerButton.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validation
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty()) {
                showMessage(messageLabel, "All fields are required", true);
                return;
            }

            if (!password.equals(confirmPassword)) {
                showMessage(messageLabel, "Passwords do not match", true);
                return;
            }

            // Register user
            AuthResult result = authService.register(username, email, password, fullName);

            if (result.isSuccess()) {
                showMessage(messageLabel, "Registration successful! Redirecting to login...", false);

                // Delay and redirect to login
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(
                                javafx.util.Duration.seconds(2),
                                event -> {
                                    LoginView loginView = new LoginView(stage);
                                    stage.setScene(loginView.createScene());
                                }));
                timeline.play();
            } else {
                showMessage(messageLabel, result.getMessage(), true);
            }
        });

        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
        });

        Scene scene = new Scene(root, 800, 750);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }

    private void showMessage(Label label, String message, boolean isError) {
        label.setText(message);
        label.getStyleClass().removeAll("message-success", "message-error");
        label.getStyleClass().add(isError ? "message-error" : "message-success");
    }
}
