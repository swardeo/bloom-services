package model;

public enum Type {
    SAVING("SAVING"),
    DEBT("DEBT");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
