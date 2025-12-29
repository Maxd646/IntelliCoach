package com.services;

import com.models.ActivityType;
import com.models.ActivitySession;
import java.time.LocalDate;
import java.util.*;

/**
 * Analytics Service - Analyzes time usage and compares with recommendations
 */
public class AnalyticsService {
    private TimeTrackingService timeTrackingService;

    public AnalyticsService() {
        this.timeTrackingService = new TimeTrackingService();
    }

    /**
     * Get daily analytics for a specific date
     */
    public DailyAnalytics getDailyAnalytics(int userId, LocalDate date) {
        Map<String, Integer> actualTime = timeTrackingService.getTimeByActivity(userId, date);
        return new DailyAnalytics(date, actualTime);
    }

    /**
     * Get weekly analytics
     */
    public WeeklyAnalytics getWeeklyAnalytics(int userId, LocalDate endDate) {
        LocalDate startDate = endDate.minusDays(6); // Last 7 days
        List<ActivitySession> sessions = timeTrackingService.getSessionsByDateRange(userId, startDate, endDate);

        return new WeeklyAnalytics(startDate, endDate, sessions);
    }

    /**
     * Get monthly analytics
     */
    public MonthlyAnalytics getMonthlyAnalytics(int userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<ActivitySession> sessions = timeTrackingService.getSessionsByDateRange(userId, startDate, endDate);

        return new MonthlyAnalytics(year, month, sessions);
    }

    /**
     * Daily Analytics class
     */
    public static class DailyAnalytics {
        private LocalDate date;
        private Map<String, Integer> actualMinutes;
        private Map<String, ComparisonResult> comparisons;
        private int totalMinutes;

        public DailyAnalytics(LocalDate date, Map<String, Integer> actualMinutes) {
            this.date = date;
            this.actualMinutes = actualMinutes;
            this.totalMinutes = actualMinutes.values().stream().mapToInt(Integer::intValue).sum();
            this.comparisons = new HashMap<>();

            // Compare with recommended times
            for (ActivityType type : ActivityType.values()) {
                String typeName = type.getDisplayName();
                int actual = actualMinutes.getOrDefault(typeName, 0);
                comparisons.put(typeName, new ComparisonResult(
                        typeName, actual, type.getMinMinutes(), type.getMaxMinutes()));
            }
        }

        public LocalDate getDate() {
            return date;
        }

        public Map<String, Integer> getActualMinutes() {
            return actualMinutes;
        }

        public Map<String, ComparisonResult> getComparisons() {
            return comparisons;
        }

        public int getTotalMinutes() {
            return totalMinutes;
        }

        public double getTotalHours() {
            return totalMinutes / 60.0;
        }

        public int getActualTime(String activityType) {
            return actualMinutes.getOrDefault(activityType, 0);
        }

        public ComparisonResult getComparison(String activityType) {
            return comparisons.get(activityType);
        }
    }

    /**
     * Weekly Analytics class
     */
    public static class WeeklyAnalytics {
        private LocalDate startDate;
        private LocalDate endDate;
        private Map<String, Integer> totalMinutes;
        private Map<String, Double> averageMinutesPerDay;
        private List<DailyAnalytics> dailyAnalytics;

        public WeeklyAnalytics(LocalDate startDate, LocalDate endDate, List<ActivitySession> sessions) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalMinutes = new HashMap<>();
            this.averageMinutesPerDay = new HashMap<>();

            // Calculate totals
            for (ActivitySession session : sessions) {
                if (session.getDurationMinutes() > 0) {
                    String type = session.getActivityType();
                    totalMinutes.put(type, totalMinutes.getOrDefault(type, 0) + session.getDurationMinutes());
                }
            }

            // Calculate averages
            int days = 7;
            for (Map.Entry<String, Integer> entry : totalMinutes.entrySet()) {
                averageMinutesPerDay.put(entry.getKey(), entry.getValue() / (double) days);
            }
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public Map<String, Integer> getTotalMinutes() {
            return totalMinutes;
        }

        public Map<String, Double> getAverageMinutesPerDay() {
            return averageMinutesPerDay;
        }

        public int getTotalTime(String activityType) {
            return totalMinutes.getOrDefault(activityType, 0);
        }

        public double getAverageTime(String activityType) {
            return averageMinutesPerDay.getOrDefault(activityType, 0.0);
        }
    }

    /**
     * Monthly Analytics class
     */
    public static class MonthlyAnalytics {
        private int year;
        private int month;
        private Map<String, Integer> totalMinutes;
        private Map<String, Double> averageMinutesPerDay;
        private int totalDays;

        public MonthlyAnalytics(int year, int month, List<ActivitySession> sessions) {
            this.year = year;
            this.month = month;
            this.totalMinutes = new HashMap<>();
            this.averageMinutesPerDay = new HashMap<>();

            LocalDate firstDay = LocalDate.of(year, month, 1);
            this.totalDays = firstDay.lengthOfMonth();

            // Calculate totals
            for (ActivitySession session : sessions) {
                if (session.getDurationMinutes() > 0) {
                    String type = session.getActivityType();
                    totalMinutes.put(type, totalMinutes.getOrDefault(type, 0) + session.getDurationMinutes());
                }
            }

            // Calculate averages
            for (Map.Entry<String, Integer> entry : totalMinutes.entrySet()) {
                averageMinutesPerDay.put(entry.getKey(), entry.getValue() / (double) totalDays);
            }
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public Map<String, Integer> getTotalMinutes() {
            return totalMinutes;
        }

        public Map<String, Double> getAverageMinutesPerDay() {
            return averageMinutesPerDay;
        }

        public int getTotalDays() {
            return totalDays;
        }
    }

    /**
     * Comparison Result class
     */
    public static class ComparisonResult {
        private String activityType;
        private int actualMinutes;
        private int recommendedMin;
        private int recommendedMax;
        private String status; // "LOW", "OPTIMAL", "HIGH"
        private int differenceMinutes;

        public ComparisonResult(String activityType, int actualMinutes, int recommendedMin, int recommendedMax) {
            this.activityType = activityType;
            this.actualMinutes = actualMinutes;
            this.recommendedMin = recommendedMin;
            this.recommendedMax = recommendedMax;

            // Determine status
            if (actualMinutes < recommendedMin) {
                this.status = "LOW";
                this.differenceMinutes = recommendedMin - actualMinutes;
            } else if (actualMinutes > recommendedMax) {
                this.status = "HIGH";
                this.differenceMinutes = actualMinutes - recommendedMax;
            } else {
                this.status = "OPTIMAL";
                this.differenceMinutes = 0;
            }
        }

        public String getActivityType() {
            return activityType;
        }

        public int getActualMinutes() {
            return actualMinutes;
        }

        public double getActualHours() {
            return actualMinutes / 60.0;
        }

        public int getRecommendedMin() {
            return recommendedMin;
        }

        public int getRecommendedMax() {
            return recommendedMax;
        }

        public double getRecommendedMinHours() {
            return recommendedMin / 60.0;
        }

        public double getRecommendedMaxHours() {
            return recommendedMax / 60.0;
        }

        public String getStatus() {
            return status;
        }

        public int getDifferenceMinutes() {
            return differenceMinutes;
        }

        public double getDifferenceHours() {
            return differenceMinutes / 60.0;
        }

        public boolean isLow() {
            return "LOW".equals(status);
        }

        public boolean isHigh() {
            return "HIGH".equals(status);
        }

        public boolean isOptimal() {
            return "OPTIMAL".equals(status);
        }
    }
}
