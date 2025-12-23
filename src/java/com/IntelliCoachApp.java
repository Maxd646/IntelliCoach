package com;

import com.views.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class IntelliCoachApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IntelliCoach");
        primaryStage.setX(210);
        primaryStage.setY(30);
        primaryStage.setMinWidth(100);
        primaryStage.setMinHeight(100);
        primaryStage.setResizable(true);

        // Start with login view
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.createScene());
        primaryStage.show();

        System.out.println(" IntelliCoach Application started");
    }

    @Override
    public void stop() {
        System.out.println(" IntelliCoach Application stopped");
    }

    public static void main(String[] args) {

        launch(args);
    }
}
