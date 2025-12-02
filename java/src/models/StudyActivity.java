package models;

public class StudyActivity extends Activity {

    private String subject;

    public StudyActivity(String id, String userId, double hours, String subject) {
        super(id, userId, hours, "Study");
        this.subject = subject;
    }

    @Override
    public String toDisplayString() {
        return null;
    }
}
