package com.services;

import com.models.Recommendation;
import com.services.AnalyticsService.DailyAnalytics;
import com.services.AnalyticsService.ComparisonResult;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Recommendation Service - Generates personalized recommendations based on time usage
 */
public class RecommendationService {
    private AnalyticsService analyticsService;
    
    public RecommendationService() {
        this.analyticsService = new AnalyticsService();
    }
    
    /**
     * Generate recommendations for a specific date
     */
    public List<Recommendation> generateRecommendations(int userId, LocalDate date) {
        List<Recommendation> recommendations = new ArrayList<>();
        DailyAnalytics analytics = analyticsService.getDailyAnalytics(userId, date);
        
        // Check each activity type
        for (ComparisonResult comparison : analytics.getComparisons().values()) {
            String activityType = comparison.getActivityType();
            
            if (comparison.isLow()) {
                // Activity time is below recommended
                recommendations.add(createLowTimeRecommendation(userId, activityType, comparison, date));
            } else if (comparison.isHigh()) {
                // Activity time exceeds recommended
                recommendations.add(createHighTimeRecommendation(userId, activityType, comparison, date));
            }
        }
        
        // Special check for Sleep (mandatory and critical)
        ComparisonResult sleepComparison = analytics.getComparison("Sleep");
        if (sleepComparison != null && sleepComparison.isLow()) {
            recommendations.add(createCriticalSleepRecommendation(userId, sleepComparison, date));
        }
        
        // Check for balance issues
        checkBalanceIssues(userId, analytics, recommendations, date);
        
        return recommendations;
    }
    
    /**
     * Create recommendation for low activity time
     */
    private Recommendation createLowTimeRecommendation(int userId, String activityType, 
                                                      ComparisonResult comparison, LocalDate date) {
        String text = String.format(
            "Your %s time is below recommended levels. You spent %.1f hours, but the recommended range is %.1f-%.1f hours. " +
            "Consider increasing by %.1f hours.",
            activityType,
            comparison.getActualHours(),
            comparison.getRecommendedMinHours(),
            comparison.getRecommendedMaxHours(),
            comparison.getDifferenceHours()
        );
        
        String priority = activityType.equals("Sleep") || activityType.equals("Academic") ? "HIGH" : "MEDIUM";
        
        return new Recommendation(userId, text, "LOW_TIME", priority, date);
    }
    
    /**
     * Create recommendation for high activity time
     */
    private Recommendation createHighTimeRecommendation(int userId, String activityType, 
                                                       ComparisonResult comparison, LocalDate date) {
        String text = String.format(
            "Your %s time exceeds recommended levels. You spent %.1f hours, but the recommended range is %.1f-%.1f hours. " +
            "Consider reducing by %.1f hours for better balance.",
            activityType,
            comparison.getActualHours(),
            comparison.getRecommendedMinHours(),
            comparison.getRecommendedMaxHours(),
            comparison.getDifferenceHours()
        );
        
        String priority = activityType.equals("Entertainment") ? "HIGH" : "MEDIUM";
        
        return new Recommendation(userId, text, "HIGH_TIME", priority, date);
    }
    
    /**
     * Create critical recommendation for low sleep
     */
    private Recommendation createCriticalSleepRecommendation(int userId, ComparisonResult comparison, LocalDate date) {
        String text = String.format(
            "CRITICAL: You slept only %.1f hours, which is below the minimum recommended %.1f hours. " +
            "Insufficient sleep affects productivity, health, and academic performance. " +
            "Please prioritize getting at least %.1f hours of sleep tonight.",
            comparison.getActualHours(),
            comparison.getRecommendedMinHours(),
            comparison.getRecommendedMinHours()
        );
        
        return new Recommendation(userId, text, "CRITICAL_SLEEP", "HIGH", date);
    }
    
    /**
     * Check for balance issues across activities
     */
    private void checkBalanceIssues(int userId, DailyAnalytics analytics, 
                                   List<Recommendation> recommendations, LocalDate date) {
        
        ComparisonResult academic = analytics.getComparison("Academic");
        ComparisonResult entertainment = analytics.getComparison("Entertainment");
        ComparisonResult sport = analytics.getComparison("Sport");
        
        // Check if entertainment is high while academic is low
        if (academic != null && entertainment != null) {
            if (academic.isLow() && entertainment.isHigh()) {
                String text = String.format(
                    "Balance Alert: You spent %.1f hours on entertainment but only %.1f hours on academics. " +
                    "Consider reallocating some entertainment time to academic activities for better productivity.",
                    entertainment.getActualHours(),
                    academic.getActualHours()
                );
                recommendations.add(new Recommendation(userId, text, "BALANCE_ISSUE", "HIGH", date));
            }
        }
        
        // Check if no sport activity
        if (sport != null && sport.getActualMinutes() == 0) {
            String text = "You haven't logged any sport/exercise activity today. " +
                         "Physical activity is important for health and mental clarity. " +
                         "Try to include at least 30-60 minutes of exercise.";
            recommendations.add(new Recommendation(userId, text, "NO_SPORT", "MEDIUM", date));
        }
        
        // Check for long academic sessions without breaks
        if (academic != null && academic.getActualMinutes() > 480) { // More than 8 hours
            String text = String.format(
                "You've spent %.1f hours on academic activities. " +
                "Remember to take regular breaks to maintain focus and avoid burnout. " +
                "Consider the Pomodoro technique: 25 minutes work, 5 minutes break.",
                academic.getActualHours()
            );
            recommendations.add(new Recommendation(userId, text, "LONG_SESSION", "MEDIUM", date));
        }
    }
    
    /**
     * Get productivity score (0-100) based on how well time is allocated
     */
    public int getProductivityScore(int userId, LocalDate date) {
        DailyAnalytics analytics = analyticsService.getDailyAnalytics(userId, date);
        int score = 100;
        
        for (ComparisonResult comparison : analytics.getComparisons().values()) {
            if (comparison.isLow() || comparison.isHigh()) {
                // Deduct points based on how far from optimal
                int deduction = (int) (comparison.getDifferenceMinutes() / 30.0 * 5); // 5 points per 30 min difference
                deduction = Math.min(deduction, 20); // Max 20 points deduction per activity
                
                // Double deduction for Sleep and Academic
                if (comparison.getActivityType().equals("Sleep") || 
                    comparison.getActivityType().equals("Academic")) {
                    deduction *= 2;
                }
                
                score -= deduction;
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }
}
