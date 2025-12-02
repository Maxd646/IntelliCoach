package models;

public class CustomActivity extends Activity {

    private String category;

    public CustomActivity(String id, String userId, double hours, String category) {
        super(id, userId, hours, "Custom");
        this.category = category;
    }

    @Override
    public String toDisplayString() {
        return null;
    }
}
