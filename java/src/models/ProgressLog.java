package models;

import java.time.LocalDate;

public class ProgressLog extends BaseModel {

    private LocalDate date;
    private double totalHours;

    public ProgressLog(String id, LocalDate date, double totalHours) {
        super(id);
        this.date = date;
        this.totalHours = totalHours;
    }

    @Override
    public String toDisplayString() {
        return null;
    }
}
