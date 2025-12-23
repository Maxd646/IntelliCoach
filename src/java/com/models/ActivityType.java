package com.models;

/**
 * Predefined activity categories (mandatory)
 */
public enum ActivityType {
    ACADEMIC("Academic", 360, 480),           // 6-8 hours
    SPORT("Sport", 60, 120),                  // 1-2 hours
    ENTERTAINMENT("Entertainment", 60, 120),   // 1-2 hours
    EXTRA_ACTIVITY("Extra Activity", 60, 120), // 1-2 hours
    SLEEP("Sleep", 420, 540),                 // 7-9 hours (MANDATORY)
    HEALTH_HYGIENE("Health / Hygiene", 30, 60); // 30-60 minutes
    
    private final String displayName;
    private final int minMinutes;
    private final int maxMinutes;
    
    ActivityType(String displayName, int minMinutes, int maxMinutes) {
        this.displayName = displayName;
        this.minMinutes = minMinutes;
        this.maxMinutes = maxMinutes;
    }
    
    public String getDisplayName() { return displayName; }
    public int getMinMinutes() { return minMinutes; }
    public int getMaxMinutes() { return maxMinutes; }
    public double getMinHours() { return minMinutes / 60.0; }
    public double getMaxHours() { return maxMinutes / 60.0; }
    
    /**
     * Get ActivityType from display name
     */
    public static ActivityType fromDisplayName(String displayName) {
        for (ActivityType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown activity type: " + displayName);
    }
    
    /**
     * Get all display names for UI
     */
    public static String[] getAllDisplayNames() {
        ActivityType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].displayName;
        }
        return names;
    }
}
