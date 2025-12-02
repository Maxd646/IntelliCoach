package models;

import java.util.List;

public class User extends BaseModel {

    private String name;
    private int age;
    private String email;
    private String password;

    private List<Activity> activities;
    private List<ProgressLog> logs;

    public User(String id, String name, int age, String email, String password) {
        super(id);
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public void addActivity(Activity a) {
    }

    public void removeActivity(String activityId) {
    }

    public void addLog(ProgressLog log) {
    }

    @Override
    public String toDisplayString() {
        return null;
    }
}
