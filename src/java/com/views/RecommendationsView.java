package com.views;

import com.models.User;
import com.models.Recommendation;
import com.services.RecommendationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Recommendations View - Shows personalized recommendations
 */
public class RecommendationsView {
    private Stage stage;
    private User user;
    private RecommendationService recommendationService;

    private DatePicker datePicker;
    private VBox recommendationsBox;
    private Label productivityScoreLabel;

    public RecommendationsView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.recommendationService = new RecommendationService();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("background-light");

        root.setTop(createHeader());
        root.setCenter(createCenterPanel());
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-purple");

        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);

        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("btn", "btn-white-purple");
        backButton.setOnAction(e -> {
            DashboardView dashboardView = new DashboardView(stage, user);
            stage.setScene(dashboardView.createScene());
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label titleLabel = new Label("Personalized Recommendations");
        titleLabel.getStyleClass().addAll("title-medium", "title-white");

        headerContent.getChildren().addAll(backButton, spacer1, titleLabel, spacer2);
        header.getChildren().add(headerContent);

        return header;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(20);
        centerPanel.setPadding(new Insets(30));

        // Controls
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("Select Date:");
        dateLabel.setFont(Font.font("Arial", 14));

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-font-size: 14px;");

        Button generateButton = new Button(" Generate Recommendations");
        generateButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px;");
        generateButton.setOnAction(e -> loadRecommendations());

        controlsBox.getChildren().addAll(dateLabel, datePicker, generateButton);

        // Productivity score
        VBox scoreBox = createScoreBox();

        // Recommendations list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5;");

        recommendationsBox = new VBox(15);
        recommendationsBox.setPadding(new Insets(20));
        scrollPane.setContent(recommendationsBox);

        centerPanel.getChildren().addAll(controlsBox, scoreBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Load initial recommendations
        loadRecommendations();

        return centerPanel;
    }

    private VBox createScoreBox() {
        VBox scoreBox = new VBox(10);
        scoreBox.setPadding(new Insets(20));
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label titleLabel = new Label("Productivity Score");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        productivityScoreLabel = new Label("--");
        productivityScoreLabel.getStyleClass().add("score-excellent");

        Label descLabel = new Label("Based on time allocation vs recommended values");
        descLabel.setFont(Font.font("Arial", 12));
        descLabel.setStyle("-fx-text-fill: #666;");

        scoreBox.getChildren().addAll(titleLabel, productivityScoreLabel, descLabel);
        return scoreBox;
    }

    private void loadRecommendations() {
        LocalDate selectedDate = datePicker.getValue();

        // Get productivity score
        int score = recommendationService.getProductivityScore(user.getUserId(), selectedDate);
        productivityScoreLabel.setText(score + "/100");

        // Color code the score
        productivityScoreLabel.getStyleClass().removeAll("score-excellent", "score-good", "score-poor");
        if (score >= 80) {
            productivityScoreLabel.getStyleClass().add("score-excellent");
        } else if (score >= 60) {
            productivityScoreLabel.getStyleClass().add("score-good");
        } else {
            productivityScoreLabel.getStyleClass().add("score-poor");
        }

        // Get recommendations
        List<Recommendation> recommendations = recommendationService.generateRecommendations(
                user.getUserId(), selectedDate);

        // Clear existing recommendations
        recommendationsBox.getChildren().clear();

        Label headerLabel = new Label("Recommendations for " + selectedDate.toString() + ":");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        recommendationsBox.getChildren().add(headerLabel);

        if (recommendations.isEmpty()) {
            Label noRecommendationsLabel = new Label("🎉 Great job! Your time allocation is optimal.");
            noRecommendationsLabel.setFont(Font.font("Arial", 16));
            noRecommendationsLabel.setStyle("-fx-text-fill: #4CAF50;");
            noRecommendationsLabel.setPadding(new Insets(20));
            recommendationsBox.getChildren().add(noRecommendationsLabel);
        } else {
            for (Recommendation recommendation : recommendations) {
                VBox recommendationCard = createRecommendationCard(recommendation);
                recommendationsBox.getChildren().add(recommendationCard);
            }
        }
    }

    private VBox createRecommendationCard(Recommendation recommendation) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("recommendation-card");

        // Priority indicator
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label priorityLabel = new Label(recommendation.getPriority());
        priorityLabel.setPadding(new Insets(5, 10, 5, 10));
        priorityLabel.getStyleClass().add("priority-badge");

        if ("HIGH".equals(recommendation.getPriority())) {
            priorityLabel.getStyleClass().add("priority-high");
        } else if ("MEDIUM".equals(recommendation.getPriority())) {
            priorityLabel.getStyleClass().add("priority-medium");
        } else {
            priorityLabel.getStyleClass().add("priority-low");
        }

        Label typeLabel = new Label(recommendation.getRecommendationType().replace("_", " "));
        typeLabel.getStyleClass().add("recommendation-type");

        headerBox.getChildren().addAll(priorityLabel, typeLabel);

        // Recommendation text
        Label textLabel = new Label(recommendation.getRecommendationText());
        textLabel.setWrapText(true);
        textLabel.getStyleClass().add("recommendation-text");

        // Icon based on type
        String icon = getIconForType(recommendation.getRecommendationType());
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("recommendation-icon");

        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.getChildren().addAll(iconLabel, textLabel);

        card.getChildren().addAll(headerBox, contentBox);

        return card;
    }

    private String getIconForType(String type) {
        return switch (type) {
            case "LOW_TIME" -> "⬇";
            case "HIGH_TIME" -> "⬆";
            case "CRITICAL_SLEEP" -> "";
            case "BALANCE_ISSUE" -> "";
            case "NO_SPORT" -> "";
            case "LONG_SESSION" -> "";
            default -> "";
        };
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("dashboard-footer");
        
        Label footerLabel = new Label("© 2024 IntelliCoach - Personalized Recommendations for Better Productivity");
        footerLabel.getStyleClass().add("footer-text");
        
        footer.getChildren().add(footerLabel);
        return footer;
    }
}
