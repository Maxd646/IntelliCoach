package com.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Recommendation model - system-generated suggestions based on time usage
 */
public class Recommendation {
    private int recommendationId;
    private int userId;
    private String recommendationText;
    private String recommendationType;
    private String priority; // HIGH, MEDIUM, LOW
    private LocalDate basedOnDate;
    private LocalDateTime createdAt;
    private boolean isRead;
    
    public Recommendation() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.priority = "MEDIUM";
    }
    
    public Recommendation(int userId, String recommendationText, String recommendationType, 
                         String priority, LocalDate basedOnDate) {
        this();
        this.userId = userId;
        this.recommendationText = recommendationText;
        this.recommendationType = recommendationType;
        this.priority = priority;
        this.basedOnDate = basedOnDate;
    }
    
    // Getters and setters
    public int getRecommendationId() { return recommendationId; }
    public void setRecommendationId(int recommendationId) { this.recommendationId = recommendationId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { 
        this.recommendationText = recommendationText; 
    }
    
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { 
        this.recommendationType = recommendationType; 
    }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDate getBasedOnDate() { return basedOnDate; }
    public void setBasedOnDate(LocalDate basedOnDate) { this.basedOnDate = basedOnDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    @Override
    public String toString() {
        return String.format("Recommendation{type='%s', priority='%s', text='%s'}", 
            recommendationType, priority, recommendationText);
    }
}
