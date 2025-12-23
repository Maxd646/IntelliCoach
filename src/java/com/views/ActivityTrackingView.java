package com.views;

import com.models.User;
import com.models.ActivityType;
import com.models.ActivitySession;
import com.services.TimeTrackingService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
public class ActivityTrackingView {
    private Stage stage;
    private User user;
    private TimeTrackingService timeTrackingService;
    
    private ComboBox<String> activityComboBox;
    private Button startButton;
    private Button stopButton;
    private Label statusLabel;
    private Label timerLabel;
    private Label currentActivityLabel;
    private TableView<ActivitySession> sessionsTable;
    private VBox todayStatsBox;
    
    private Timeline timer;
    private LocalDateTime sessionStartTime;
    
    public ActivityTrackingView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.timeTrackingService = new TimeTrackingService();
    }
    
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("background-light");
        
        // Top: Header
        root.setTop(createHeader());
        
        // Center: Activity controls and sessions
        root.setCenter(createCenterPanel());
        
        // Right: Today's statistics
        root.setRight(createStatsPanel());
        
        // Bottom: Footer
        root.setBottom(createFooter());
        
        // Check for active session on load
        checkActiveSession();
        
        Scene scene = new Scene(root, 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-success");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);
        
        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("btn", "btn-white-success");
        backButton.setOnAction(e -> {
            // Stop timer if running
            if (timer != null) {
                timer.stop();
            }
            DashboardView dashboardView = new DashboardView(stage, user);
            stage.setScene(dashboardView.createScene());
        });
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Label titleLabel = new Label("Activity Tracking");
        titleLabel.getStyleClass().addAll("title-medium", "title-white");
        
        headerContent.getChildren().addAll(backButton, spacer1, titleLabel, spacer2);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(20);
        centerPanel.setPadding(new Insets(30));
        
        // Activity selection and control
        VBox controlBox = createControlBox();
        
        // Current session status
        VBox statusBox = createStatusBox();
        
        // Today's sessions table
        VBox tableBox = createTableBox();
        
        centerPanel.getChildren().addAll(controlBox, statusBox, tableBox);
        return centerPanel;
    }
    
    private VBox createControlBox() {
        VBox controlBox = new VBox(15);
        controlBox.setPadding(new Insets(20));
        controlBox.getStyleClass().add("card");
        
        Label instructionLabel = new Label("Select Activity and Start Tracking:");
        instructionLabel.getStyleClass().add("instruction-label");
        
        // Activity selection
        HBox selectionBox = new HBox(15);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        
        Label activityLabel = new Label("Activity:");
        activityLabel.getStyleClass().add("text-normal");
        
        activityComboBox = new ComboBox<>();
        activityComboBox.getItems().addAll(ActivityType.getAllDisplayNames());
        activityComboBox.setValue(ActivityType.ACADEMIC.getDisplayName());
        activityComboBox.setPrefWidth(200);
        activityComboBox.getStyleClass().add("combo-box");
        
        selectionBox.getChildren().addAll(activityLabel, activityComboBox);
        
        // Start/Stop buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        startButton = new Button(" START");
        startButton.setPrefWidth(150);
        startButton.setPrefHeight(50);
        startButton.getStyleClass().add("btn-start");
        
        stopButton = new Button(" STOP");
        stopButton.setPrefWidth(150);
        stopButton.setPrefHeight(50);
        stopButton.getStyleClass().add("btn-stop");
        stopButton.setDisable(true);
        
        buttonBox.getChildren().addAll(startButton, stopButton);
        
        // Event handlers using lambda expressions
        startButton.setOnAction(e -> startActivity());
        stopButton.setOnAction(e -> stopActivity());
        
        controlBox.getChildren().addAll(instructionLabel, selectionBox, buttonBox);
        return controlBox;
    }
    
    private VBox createStatusBox() {
        VBox statusBox = new VBox(10);
        statusBox.setPadding(new Insets(20));
        statusBox.setAlignment(Pos.CENTER);
        statusBox.getStyleClass().add("card-blue");
        
        currentActivityLabel = new Label("No active activity");
        currentActivityLabel.getStyleClass().add("timer-activity");
        
        timerLabel = new Label("00:00:00");
        timerLabel.getStyleClass().add("timer-label");
        
        statusLabel = new Label("Ready to start tracking");
        statusLabel.getStyleClass().add("timer-status");
        
        statusBox.getChildren().addAll(currentActivityLabel, timerLabel, statusLabel);
        return statusBox;
    }
    
    private VBox createTableBox() {
        VBox tableBox = new VBox(10);
        
        Label tableTitle = new Label("Today's Sessions:");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        sessionsTable = new TableView<>();
        sessionsTable.setPrefHeight(250);
        
        // Activity column
        TableColumn<ActivitySession, String> activityCol = new TableColumn<>("Activity");
        activityCol.setPrefWidth(150);
        activityCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActivityType()));
        
        // Start time column
        TableColumn<ActivitySession, String> startCol = new TableColumn<>("Start Time");
        startCol.setPrefWidth(120);
        startCol.setCellValueFactory(cellData -> {
            LocalDateTime start = cellData.getValue().getStartTime();
            return new javafx.beans.property.SimpleStringProperty(
                start != null ? start.toLocalTime().toString() : "-");
        });
        
        // End time column
        TableColumn<ActivitySession, String> endCol = new TableColumn<>("End Time");
        endCol.setPrefWidth(120);
        endCol.setCellValueFactory(cellData -> {
            LocalDateTime end = cellData.getValue().getEndTime();
            return new javafx.beans.property.SimpleStringProperty(
                end != null ? end.toLocalTime().toString() : "Active");
        });
        
        // Duration column
        TableColumn<ActivitySession, String> durationCol = new TableColumn<>("Duration");
        durationCol.setPrefWidth(100);
        durationCol.setCellValueFactory(cellData -> {
            int minutes = cellData.getValue().getDurationMinutes();
            String duration = minutes > 0 ? 
                String.format("%dh %dm", minutes / 60, minutes % 60) : "Active";
            return new javafx.beans.property.SimpleStringProperty(duration);
        });
        
        sessionsTable.getColumns().addAll(activityCol, startCol, endCol, durationCol);
        
        // Refresh button
        Button refreshButton = new Button(" Refresh");
        refreshButton.setOnAction(e -> loadTodaySessions());
        
        tableBox.getChildren().addAll(tableTitle, sessionsTable, refreshButton);
        return tableBox;
    }
    
    private VBox createStatsPanel() {
        todayStatsBox = new VBox(15);
        todayStatsBox.setPadding(new Insets(20));
        todayStatsBox.setPrefWidth(250);
        todayStatsBox.getStyleClass().add("stats-panel");
        
        Label statsTitle = new Label("Today's Summary");
        statsTitle.getStyleClass().add("stats-title");
        
        todayStatsBox.getChildren().add(statsTitle);
        
        updateStatsPanel();
        
        return todayStatsBox;
    }
    
    private void startActivity() {
        String selectedActivity = activityComboBox.getValue();
        
        if (selectedActivity == null) {
            showAlert("Please select an activity", Alert.AlertType.WARNING);
            return;
        }
        
        // Start the session
        ActivitySession session = timeTrackingService.startActivity(user.getUserId(), selectedActivity);
        
        if (session != null) {
            sessionStartTime = session.getStartTime();
            
            // Update UI
            startButton.setDisable(true);
            stopButton.setDisable(false);
            activityComboBox.setDisable(true);
            
            currentActivityLabel.setText("Current: " + selectedActivity);
            statusLabel.setText("Tracking in progress...");
            
            // Start timer
            startTimer();
            
            // Reload sessions
            loadTodaySessions();
            updateStatsPanel();
        } else {
            showAlert("Failed to start activity", Alert.AlertType.ERROR);
        }
    }
    
    private void stopActivity() {
        boolean success = timeTrackingService.stopActivity(user.getUserId());
        
        if (success) {
            // Stop timer
            if (timer != null) {
                timer.stop();
            }
            
            // Update UI
            startButton.setDisable(false);
            stopButton.setDisable(true);
            activityComboBox.setDisable(false);
            
            currentActivityLabel.setText("No active activity");
            timerLabel.setText("00:00:00");
            statusLabel.setText("Activity stopped and saved");
            
            sessionStartTime = null;
            
            // Reload sessions
            loadTodaySessions();
            updateStatsPanel();
            
            showAlert("Activity stopped and saved successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Failed to stop activity", Alert.AlertType.ERROR);
        }
    }
    
    private void checkActiveSession() {
        ActivitySession activeSession = timeTrackingService.getActiveSession(user.getUserId());
        
        if (activeSession != null) {
            // Resume active session
            sessionStartTime = activeSession.getStartTime();
            
            startButton.setDisable(true);
            stopButton.setDisable(false);
            activityComboBox.setDisable(true);
            activityComboBox.setValue(activeSession.getActivityType());
            
            currentActivityLabel.setText("Current: " + activeSession.getActivityType());
            statusLabel.setText("Resumed active session");
            
            startTimer();
        }
        
        loadTodaySessions();
        updateStatsPanel();
    }
    
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }
    
    private void updateTimer() {
        if (sessionStartTime != null) {
            long seconds = ChronoUnit.SECONDS.between(sessionStartTime, LocalDateTime.now());
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            
            timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, secs));
        }
    }
    
    private void loadTodaySessions() {
        List<ActivitySession> sessions = timeTrackingService.getTodaySessions(user.getUserId());
        sessionsTable.getItems().clear();
        sessionsTable.getItems().addAll(sessions);
    }
    
    private void updateStatsPanel() {
        // Clear existing stats (keep title)
        if (todayStatsBox.getChildren().size() > 1) {
            todayStatsBox.getChildren().remove(1, todayStatsBox.getChildren().size());
        }
        
        Map<String, Integer> timeByActivity = timeTrackingService.getTodayTimeByActivity(user.getUserId());
        int totalMinutes = timeByActivity.values().stream().mapToInt(Integer::intValue).sum();
        
        // Total time
        Label totalLabel = new Label(String.format("Total: %.1f hours", totalMinutes / 60.0));
        totalLabel.getStyleClass().add("stats-total");
        todayStatsBox.getChildren().add(totalLabel);
        
        // Separator
        Separator separator = new Separator();
        todayStatsBox.getChildren().add(separator);
        
        // Time per activity
        for (ActivityType type : ActivityType.values()) {
            String typeName = type.getDisplayName();
            int minutes = timeByActivity.getOrDefault(typeName, 0);
            
            if (minutes > 0) {
                VBox activityBox = new VBox(3);
                
                Label nameLabel = new Label(typeName);
                nameLabel.getStyleClass().add("stats-activity-name");
                
                Label timeLabel = new Label(String.format("%.1f hours (%d min)", 
                    minutes / 60.0, minutes));
                timeLabel.getStyleClass().add("stats-activity-time");
                
                activityBox.getChildren().addAll(nameLabel, timeLabel);
                todayStatsBox.getChildren().add(activityBox);
            }
        }
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("dashboard-footer");
        
        Label footerLabel = new Label("© 2024 IntelliCoach - Track Your Time, Achieve Your Goals");
        footerLabel.getStyleClass().add("footer-text");
        
        footer.getChildren().add(footerLabel);
        return footer;
    }
    
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Activity Tracking");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
