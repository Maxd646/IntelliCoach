package services;

import models.Activity;
import models.ProgressLog;
import java.util.List;

public class BasicRecommendationStrategy implements RecommendationStrategy {

    @Override
    public String giveRecommendation(List<Activity> activities, List<ProgressLog> logs) {
        return null;
    }
}
