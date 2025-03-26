public enum FlightStatus {
    SCHEDULED("As Per Schedule"),
    CANCELLED("Cancelled");

    private final String displayName;

    FlightStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}