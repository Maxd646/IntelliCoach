package models;

import java.time.LocalDate;

public abstract class Activity extends BaseModel {

    protected String userId;
    protected double hours;
    protected LocalDate date;
    protected String type;

    public Activity(String id, String userId, double hours, String type) {
        super(id);
        this.userId = userId;
        this.hours = hours;
        this.type = type;
        this.date = LocalDate.now();
    }

    public String getUserId() {
        return userId;
    }

    public double getHours() {
        return hours;
    }

    public String getType() {
        return type;
    }

    @Override
    public abstract String toDisplayString();
}
