package com.views;

import com.models.User;
import com.models.ActivitySession;
import com.services.TimeTrackingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reports View - Generate and export reports
 */
public class ReportsView {
    private Stage stage;
    private User user;
    private TimeTrackingService timeTrackingService;

    private ComboBox<String> reportTypeComboBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextArea reportPreviewArea;

    public ReportsView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.timeTrackingService = new TimeTrackingService();
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
        header.getStyleClass().add("header-warning");

        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);

        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("btn", "btn-white-warning");
        backButton.setOnAction(e -> {
            DashboardView dashboardView = new DashboardView(stage, user);
            stage.setScene(dashboardView.createScene());
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label titleLabel = new Label("Reports & Export");
        titleLabel.getStyleClass().addAll("title-medium", "title-white");

        headerContent.getChildren().addAll(backButton, spacer1, titleLabel, spacer2);
        header.getChildren().add(headerContent);

        return header;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(20);
        centerPanel.setPadding(new Insets(30));

        // Report configuration
        VBox configBox = createConfigBox();

        // Report preview
        VBox previewBox = createPreviewBox();

        // Export buttons
        HBox exportBox = createExportBox();

        centerPanel.getChildren().addAll(configBox, previewBox, exportBox);
        VBox.setVgrow(previewBox, Priority.ALWAYS);

        return centerPanel;
    }

    private VBox createConfigBox() {
        VBox configBox = new VBox(15);
        configBox.setPadding(new Insets(20));
        configBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label configLabel = new Label("Report Configuration:");
        configLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        // Report type
        Label typeLabel = new Label("Report Type:");
        reportTypeComboBox = new ComboBox<>();
        reportTypeComboBox.getItems().addAll("Daily Report", "Weekly Report", "Monthly Report", "Custom Range");
        reportTypeComboBox.setValue("Daily Report");
        reportTypeComboBox.setPrefWidth(200);
        grid.add(typeLabel, 0, 0);
        grid.add(reportTypeComboBox, 1, 0);

        // Start date
        Label startLabel = new Label("Start Date:");
        startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setPrefWidth(200);
        grid.add(startLabel, 0, 1);
        grid.add(startDatePicker, 1, 1);

        // End date
        Label endLabel = new Label("End Date:");
        endDatePicker = new DatePicker(LocalDate.now());
        endDatePicker.setPrefWidth(200);
        grid.add(endLabel, 0, 2);
        grid.add(endDatePicker, 1, 2);

        // Generate button
        Button generateButton = new Button(" Generate Report");
        generateButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        generateButton.setPrefWidth(200);
        generateButton.setOnAction(e -> generateReport());
        grid.add(generateButton, 1, 3);

        // Auto-adjust dates based on report type
        reportTypeComboBox.setOnAction(e -> {
            String type = reportTypeComboBox.getValue();
            LocalDate today = LocalDate.now();

            if ("Daily Report".equals(type)) {
                startDatePicker.setValue(today);
                endDatePicker.setValue(today);
            } else if ("Weekly Report".equals(type)) {
                startDatePicker.setValue(today.minusDays(6));
                endDatePicker.setValue(today);
            } else if ("Monthly Report".equals(type)) {
                startDatePicker.setValue(today.withDayOfMonth(1));
                endDatePicker.setValue(today);
            }
        });

        configBox.getChildren().addAll(configLabel, grid);
        return configBox;
    }

    private VBox createPreviewBox() {
        VBox previewBox = new VBox(10);

        Label previewLabel = new Label("Report Preview:");
        previewLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        reportPreviewArea = new TextArea();
        reportPreviewArea.setEditable(false);
        reportPreviewArea.setWrapText(true);
        reportPreviewArea.getStyleClass().add("report-preview");
        reportPreviewArea.setText("Click 'Generate Report' to preview...");

        previewBox.getChildren().addAll(previewLabel, reportPreviewArea);
        VBox.setVgrow(reportPreviewArea, Priority.ALWAYS);

        return previewBox;
    }

    private HBox createExportBox() {
        HBox exportBox = new HBox(15);
        exportBox.setAlignment(Pos.CENTER);

        Button exportCsvButton = new Button("💾 Export as CSV");
        exportCsvButton.setPrefWidth(150);
        exportCsvButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        exportCsvButton.setOnAction(e -> exportToCSV());

        Button exportTxtButton = new Button("📝 Export as TXT");
        exportTxtButton.setPrefWidth(150);
        exportTxtButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        exportTxtButton.setOnAction(e -> exportToTXT());

        exportBox.getChildren().addAll(exportCsvButton, exportTxtButton);
        return exportBox;
    }

    private void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate.isAfter(endDate)) {
            showAlert("Start date must be before or equal to end date", Alert.AlertType.WARNING);
            return;
        }

        List<ActivitySession> sessions = timeTrackingService.getSessionsByDateRange(
                user.getUserId(), startDate, endDate);

        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("                 INTELLICOACH TIME TRACKING REPORT\n");
        report.append("═══════════════════════════════════════════════════════════\n\n");
        report.append("User: ").append(user.getFullName()).append(" (").append(user.getUsername()).append(")\n");
        report.append("Report Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Generated: ").append(LocalDate.now()).append("\n\n");
        report.append("───────────────────────────────────────────────────────────\n\n");

        if (sessions.isEmpty()) {
            report.append("No activity sessions found for this period.\n");
        } else {
            // Summary statistics
            Map<String, Integer> totalByActivity = sessions.stream()
                    .filter(s -> s.getDurationMinutes() > 0)
                    .collect(Collectors.groupingBy(
                            ActivitySession::getActivityType,
                            Collectors.summingInt(ActivitySession::getDurationMinutes)));

            int grandTotal = totalByActivity.values().stream().mapToInt(Integer::intValue).sum();

            report.append("SUMMARY STATISTICS:\n");
            report.append("───────────────────────────────────────────────────────────\n");
            report.append(String.format("Total Sessions: %d\n", sessions.size()));
            report.append(String.format("Total Time: %.1f hours (%d minutes)\n\n",
                    grandTotal / 60.0, grandTotal));

            report.append("Time by Activity:\n");
            for (Map.Entry<String, Integer> entry : totalByActivity.entrySet()) {
                double hours = entry.getValue() / 60.0;
                double percentage = (entry.getValue() * 100.0) / grandTotal;
                report.append(String.format("  %-20s: %6.1f hours (%5.1f%%)\n",
                        entry.getKey(), hours, percentage));
            }

            report.append("\n───────────────────────────────────────────────────────────\n\n");

            // Detailed sessions
            report.append("DETAILED SESSIONS:\n");
            report.append("───────────────────────────────────────────────────────────\n");
            report.append(String.format("%-12s %-20s %-10s %-10s %-10s\n",
                    "Date", "Activity", "Start", "End", "Duration"));
            report.append("───────────────────────────────────────────────────────────\n");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            for (ActivitySession session : sessions) {
                if (session.getDurationMinutes() > 0) {
                    String date = session.getSessionDate().format(dateFormatter);
                    String activity = session.getActivityType();
                    String start = session.getStartTime().format(timeFormatter);
                    String end = session.getEndTime() != null ? session.getEndTime().format(timeFormatter) : "Active";
                    String duration = String.format("%dh %dm",
                            session.getDurationMinutes() / 60,
                            session.getDurationMinutes() % 60);

                    report.append(String.format("%-12s %-20s %-10s %-10s %-10s\n",
                            date, activity, start, end, duration));
                }
            }
        }

        report.append("\n═══════════════════════════════════════════════════════════\n");
        report.append("                    END OF REPORT\n");
        report.append("═══════════════════════════════════════════════════════════\n");

        reportPreviewArea.setText(report.toString());
    }

    private void exportToCSV() {
        if (reportPreviewArea.getText().contains("Click 'Generate Report'")) {
            showAlert("Please generate a report first", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report as CSV");
        fileChooser.setInitialFileName("intellicoach_report_" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();

                List<ActivitySession> sessions = timeTrackingService.getSessionsByDateRange(
                        user.getUserId(), startDate, endDate);

                FileWriter writer = new FileWriter(file);

                // CSV Header
                writer.write("Date,Activity,Start Time,End Time,Duration (minutes),Duration (hours)\n");

                // CSV Data
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                for (ActivitySession session : sessions) {
                    if (session.getDurationMinutes() > 0) {
                        writer.write(String.format("%s,%s,%s,%s,%d,%.2f\n",
                                session.getSessionDate().format(dateFormatter),
                                session.getActivityType(),
                                session.getStartTime().format(timeFormatter),
                                session.getEndTime() != null ? session.getEndTime().format(timeFormatter) : "Active",
                                session.getDurationMinutes(),
                                session.getDurationHours()));
                    }
                }

                writer.close();
                showAlert("Report exported successfully to:\n" + file.getAbsolutePath(),
                        Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                showAlert("Export failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void exportToTXT() {
        if (reportPreviewArea.getText().contains("Click 'Generate Report'")) {
            showAlert("Please generate a report first", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report as TXT");
        fileChooser.setInitialFileName("intellicoach_report_" + LocalDate.now() + ".txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(reportPreviewArea.getText());
                writer.close();

                showAlert("Report exported successfully to:\n" + file.getAbsolutePath(),
                        Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                showAlert("Export failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Reports");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("dashboard-footer");

        Label footerLabel = new Label("© 2025 IntelliCoach - Generate Detailed Reports & Export Your Data");
        footerLabel.getStyleClass().add("footer-text");

        footer.getChildren().add(footerLabel);
        return footer;
    }
}
