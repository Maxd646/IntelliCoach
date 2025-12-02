package controllers;

import services.*;
import models.*;

public class MainController {

    private UserService userService;
    private ActivityService activityService;
    private ProgressService progressService;
    private RecommendationService recommendationService;

    public User registerUser(String id, String name, int age, String email, String pw) {
        return null;
    }

    public User loginUser(String id, String pw) {
        return null;
    }

    public void editProfile(String id, String name, String email) {
    }

    public void deleteAccount(String id) {
    }

    public Activity logStudy(String userId, String subject, double hrs) {
        return null;
    }

    public Activity logExercise(String userId, String type, double hrs) {
        return null;
    }

    public Activity logCustom(String userId, String category, double hrs) {
        return null;
    }

    public void editActivity(String activityId) {
    }

    public void deleteActivity(String activityId) {
    }

    public ProgressLog computeDaily(String userId) {
        return null;
    }

    public void viewWeeklyProgress(String userId) {
    }

    public void viewMonthlyProgress(String userId) {
    }

    public String viewRecommendation(String userId) {
        return null;
    }

    public void switchToTimeStrategy() {
    }
}
