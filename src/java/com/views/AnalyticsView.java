package com.views;

import com.models.User;
import com.services.AnalyticsService;
import com.services.AnalyticsService.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.chart.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Analytics View - Shows time usage statistics and comparisons
 */
public class AnalyticsView {
    private Stage stage;
    private User user;
    private AnalyticsService analyticsService;

    private ComboBox<String> periodComboBox;
    private DatePicker datePicker;
    private VBox contentBox;

    public AnalyticsView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.analyticsService = new AnalyticsService();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("background-light");

        root.setTop(createHeader());
        root.setCenter(createCenterPanel());
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-primary");

        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);

        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("btn", "btn-white-primary");
        backButton.setOnAction(e -> {
            DashboardView dashboardView = new DashboardView(stage, user);
            stage.setScene(dashboardView.createScene());
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label titleLabel = new Label("Analytics & Statistics");
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

        Label periodLabel = new Label("Period:");
        periodLabel.setFont(Font.font("Arial", 14));

        periodComboBox = new ComboBox<>();
        periodComboBox.getItems().addAll("Daily", "Weekly", "Monthly");
        periodComboBox.setValue("Daily");
        periodComboBox.setStyle("-fx-font-size: 14px;");

        Label dateLabel = new Label("Date:");
        dateLabel.setFont(Font.font("Arial", 14));

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-font-size: 14px;");

        Button analyzeButton = new Button(" Analyze");
        analyzeButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        analyzeButton.setOnAction(e -> loadAnalytics());

        controlsBox.getChildren().addAll(periodLabel, periodComboBox, dateLabel, datePicker, analyzeButton);

        // Content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5;");

        contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        scrollPane.setContent(contentBox);

        centerPanel.getChildren().addAll(controlsBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Load initial analytics
        loadAnalytics();

        return centerPanel;
    }

    private void loadAnalytics() {
        String period = periodComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        contentBox.getChildren().clear();

        if ("Daily".equals(period)) {
            loadDailyAnalytics(selectedDate);
        } else if ("Weekly".equals(period)) {
            loadWeeklyAnalytics(selectedDate);
        } else if ("Monthly".equals(period)) {
            loadMonthlyAnalytics(selectedDate);
        }
    }

    private void loadDailyAnalytics(LocalDate date) {
        DailyAnalytics analytics = analyticsService.getDailyAnalytics(user.getUserId(), date);

        // Title
        Label titleLabel = new Label("Daily Analytics - " + date.toString());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        contentBox.getChildren().add(titleLabel);

        // Total time
        Label totalLabel = new Label(String.format("Total Time Tracked: %.1f hours", analytics.getTotalHours()));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setStyle("-fx-text-fill: #2196F3;");
        contentBox.getChildren().add(totalLabel);

        // Comparison table
        TableView<ComparisonResult> table = createComparisonTable(analytics.getComparisons());
        contentBox.getChildren().add(table);

        // Pie chart

        PieChart pieChart = createPieChart(analytics.getActualMinutes());
        contentBox.getChildren().add(pieChart);
    }

    private void loadWeeklyAnalytics(LocalDate endDate) {
        WeeklyAnalytics analytics = analyticsService.getWeeklyAnalytics(user.getUserId(), endDate);

        Label titleLabel = new Label(String.format("Weekly Analytics - %s to %s",
                analytics.getStartDate(), analytics.getEndDate()));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        contentBox.getChildren().add(titleLabel);

        // Bar chart
        BarChart<String, Number> barChart = createBarChart(analytics.getTotalMinutes(), "Weekly Total Time");
        contentBox.getChildren().add(barChart);

        // Average table
        TableView<Map.Entry<String, Double>> avgTable = createAverageTable(analytics.getAverageMinutesPerDay());
        contentBox.getChildren().add(avgTable);
    }

    private void loadMonthlyAnalytics(LocalDate date) {
        MonthlyAnalytics analytics = analyticsService.getMonthlyAnalytics(user.getUserId(),
                date.getYear(), date.getMonthValue());

        Label titleLabel = new Label(String.format("Monthly Analytics - %s %d",
                date.getMonth(), date.getYear()));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        contentBox.getChildren().add(titleLabel);

        Label daysLabel = new Label("Total Days: " + analytics.getTotalDays());
        daysLabel.setFont(Font.font("Arial", 14));
        contentBox.getChildren().add(daysLabel);

        // Bar chart
        BarChart<String, Number> barChart = createBarChart(analytics.getTotalMinutes(), "Monthly Total Time");
        contentBox.getChildren().add(barChart);
    }

    private TableView<ComparisonResult> createComparisonTable(Map<String, ComparisonResult> comparisons) {
        TableView<ComparisonResult> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<ComparisonResult, String> activityCol = new TableColumn<>("Activity");
        activityCol.setPrefWidth(150);
        activityCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActivityType()));

        TableColumn<ComparisonResult, String> actualCol = new TableColumn<>("Actual Time");
        actualCol.setPrefWidth(120);
        actualCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.1f hours", cellData.getValue().getActualHours())));

        TableColumn<ComparisonResult, String> recommendedCol = new TableColumn<>("Recommended");
        recommendedCol.setPrefWidth(150);
        recommendedCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.1f - %.1f hours",
                        cellData.getValue().getRecommendedMinHours(),
                        cellData.getValue().getRecommendedMaxHours())));

        TableColumn<ComparisonResult, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        // Color code status
        statusCol.setCellFactory(column -> new TableCell<ComparisonResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("LOW".equals(item)) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    } else if ("HIGH".equals(item)) {
                        setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<ComparisonResult, String> differenceCol = new TableColumn<>("Difference");
        differenceCol.setPrefWidth(120);
        differenceCol.setCellValueFactory(cellData -> {
            ComparisonResult result = cellData.getValue();
            if (result.isOptimal()) {
                return new javafx.beans.property.SimpleStringProperty("Optimal");
            } else {
                return new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f hours", result.getDifferenceHours()));
            }
        });

        table.getColumns().addAll(activityCol, actualCol, recommendedCol, statusCol, differenceCol);
        table.getItems().addAll(comparisons.values());

        return table;
    }

    private PieChart createPieChart(Map<String, Integer> timeData) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Time Distribution");
        pieChart.setPrefHeight(400);

        for (Map.Entry<String, Integer> entry : timeData.entrySet()) {
            if (entry.getValue() > 0) {
                double hours = entry.getValue() / 60.0;
                PieChart.Data slice = new PieChart.Data(
                        entry.getKey() + String.format(" (%.1fh)", hours),
                        entry.getValue());
                pieChart.getData().add(slice);
            }
        }

        return pieChart;
    }

    private BarChart<String, Number> createBarChart(Map<String, Integer> timeData, String title) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Activity");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Hours");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        barChart.setPrefHeight(400);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Time Spent");

        for (Map.Entry<String, Integer> entry : timeData.entrySet()) {
            if (entry.getValue() > 0) {
                double hours = entry.getValue() / 60.0;
                series.getData().add(new XYChart.Data<>(entry.getKey(), hours));
            }
        }

        barChart.getData().add(series);
        return barChart;
    }

    private TableView<Map.Entry<String, Double>> createAverageTable(Map<String, Double> averages) {
        TableView<Map.Entry<String, Double>> table = new TableView<>();
        table.setPrefHeight(250);

        TableColumn<Map.Entry<String, Double>, String> activityCol = new TableColumn<>("Activity");
        activityCol.setPrefWidth(200);
        activityCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey()));

        TableColumn<Map.Entry<String, Double>, String> avgCol = new TableColumn<>("Average per Day");
        avgCol.setPrefWidth(150);
        avgCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.1f hours", cellData.getValue().getValue() / 60.0)));

        table.getColumns().addAll(activityCol, avgCol);
        table.getItems().addAll(averages.entrySet());

        return table;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("dashboard-footer");

        Label footerLabel = new Label("© 2025 IntelliCoach - Analyze Your Productivity Patterns");
        footerLabel.getStyleClass().add("footer-text");

        footer.getChildren().add(footerLabel);
        return footer;
    }
}
