package services;

import models.Activity;
import models.ProgressLog;
import java.util.List;

public interface RecommendationStrategy {
    String giveRecommendation(List<Activity> activities, List<ProgressLog> logs);
}
