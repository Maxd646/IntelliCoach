package com.views;

import javafx.scene.control.Label;

import com.models.User;
import com.services.AuthenticationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Dashboard View - BorderPane layout (Chapter 7 compliant)
 * Main navigation hub
 */
public class DashboardView {
    private Stage stage;
    private User user;
    private AuthenticationService authService;

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.authService = AuthenticationService.getInstance();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("background-light");

        // Top: Header
        root.setTop(createHeader());

        // Center: Navigation buttons
        ScrollPane scrollPane = new ScrollPane(createNavigationPanel());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        root.setCenter(scrollPane);

        // Bottom: User info
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-primary");
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("IntelliCoach Dashboard");
        titleLabel.getStyleClass().addAll("title-large", "title-white");

        Label welcomeLabel = new Label("Welcome, " + user.getFullName() + "!");
        welcomeLabel.getStyleClass().addAll("subtitle", "subtitle-white");

        header.getChildren().addAll(titleLabel, welcomeLabel);
        return header;
    }

    private BorderPane createNavigationPanel() {
        BorderPane navPanel = new BorderPane();
        navPanel.setPadding(new Insets(40));

        // Left side - Welcome and instructions
        VBox leftPanel = new VBox(20);
        leftPanel.setPrefWidth(350);
        leftPanel.setPadding(new Insets(20));

        Label welcomeLabel = new Label("What would you like to do today?");
        welcomeLabel.getStyleClass().addAll("dashboard-welcome");

        Label instructionLabel = new Label(
                "Choose from the options below to get started with tracking your time and boosting productivity:");
        instructionLabel.getStyleClass().add("dashboard-instruction");
        instructionLabel.setWrapText(true);

        // Quick stats or tips
        VBox tipsBox = new VBox(10);
        tipsBox.getStyleClass().add("tips-card");
        tipsBox.setPadding(new Insets(20));

        Label tipsTitle = new Label("Quick Tips");
        tipsTitle.getStyleClass().add("tips-title");

        Label tip1 = new Label("• Track activities in real-time");
        Label tip2 = new Label("• Review weekly analytics");
        Label tip3 = new Label("• Export detailed reports");
        Label tip4 = new Label("• Get personalized recommendations");

        tip1.getStyleClass().add("tip-text");
        tip2.getStyleClass().add("tip-text");
        tip3.getStyleClass().add("tip-text");
        tip4.getStyleClass().add("tip-text");

        tipsBox.getChildren().addAll(tipsTitle, tip1, tip2, tip3, tip4);

        leftPanel.getChildren().addAll(welcomeLabel, instructionLabel, tipsBox);

        // Right side - Navigation buttons
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(450);
        rightPanel.setAlignment(Pos.CENTER);

        // Navigation buttons with enhanced styling
        Button activityButton = createModernNavButton("", "Activity Tracking",
                "time tracking for your daily activities", "nav-card-success");
        Button analyticsButton = createModernNavButton("", "Analytics Dashboard",
                "View your time usage statistics and trends", "nav-card-primary");
        Button reportsButton = createModernNavButton("", "Reports & Export",
                "Generate and export detailed reports", "nav-card-warning");
        Button recommendationsButton = createModernNavButton("", "Smart Recommendations",
                "Get personalized suggestions to improve productivity", "nav-card-purple");
        Button logoutButton = createModernNavButton("", "Sign Out",
                "Logout from your account", "nav-card-danger");

        // Event handlers using lambda expressions
        activityButton.setOnAction(e -> {
            ActivityTrackingView activityView = new ActivityTrackingView(stage, user);
            stage.setScene(activityView.createScene());
        });

        analyticsButton.setOnAction(e -> {
            AnalyticsView analyticsView = new AnalyticsView(stage, user);
            stage.setScene(analyticsView.createScene());
        });

        reportsButton.setOnAction(e -> {
            ReportsView reportsView = new ReportsView(stage, user);
            stage.setScene(reportsView.createScene());
        });

        recommendationsButton.setOnAction(e -> {
            RecommendationsView recommendationsView = new RecommendationsView(stage, user);
            stage.setScene(recommendationsView.createScene());
        });

        logoutButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Logout");
            confirm.setHeaderText("Are you sure you want to logout?");
            confirm.setContentText("All activity data has been saved.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    authService.logout();
                    LoginView loginView = new LoginView(stage);
                    stage.setScene(loginView.createScene());
                }
            });
        });

        rightPanel.getChildren().addAll(activityButton, analyticsButton,
                reportsButton, recommendationsButton, logoutButton);

        navPanel.setLeft(leftPanel);
        navPanel.setRight(rightPanel);

        return navPanel;
    }

    private Button createModernNavButton(String icon, String title, String description, String styleClass) {
        HBox buttonContent = new HBox(20);
        buttonContent.setAlignment(Pos.CENTER_LEFT);
        buttonContent.setPadding(new Insets(20));

        // Icon section
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("nav-icon");
        iconLabel.setPrefWidth(50);

        // Text section
        VBox textBox = new VBox(5);
        textBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("nav-card-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("nav-card-desc");
        descLabel.setWrapText(true);

        textBox.getChildren().addAll(titleLabel, descLabel);

        // Arrow indicator
        Label arrowLabel = new Label("→");
        arrowLabel.getStyleClass().add("nav-arrow");

        buttonContent.getChildren().addAll(iconLabel, textBox, arrowLabel);

        Button button = new Button();
        button.setGraphic(buttonContent);
        button.setPrefWidth(420);
        button.setPrefHeight(90);
        button.getStyleClass().addAll("nav-card", styleClass);

        return button;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(20));
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("dashboard-footer");

        Label footerLabel = new Label("© 2024 IntelliCoach - Empowering Your Productivity Journey");
        footerLabel.getStyleClass().add("footer-text");

        footer.getChildren().add(footerLabel);
        return footer;
    }
}
