package models;

public class ExerciseActivity extends Activity {

    private String exerciseType;

    public ExerciseActivity(String id, String userId, double hours, String exerciseType) {
        super(id, userId, hours, "Exercise");
        this.exerciseType = exerciseType;
    }

    @Override
    public String toDisplayString() {
        return null;
    }
}
