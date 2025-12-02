package services;

import models.Activity;
import models.ProgressLog;
import java.util.List;

public class RecommendationService {

    private RecommendationStrategy strategy;

    public RecommendationService(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public String getRecommendation(List<Activity> activities, List<ProgressLog> logs) {
        return null;
    }
}
