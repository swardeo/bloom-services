package model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HealthResponse {
    HEALTHY("healthy"),
    UNHEALTHY("unhealthy");

    private final String health;

    HealthResponse(String health) {
        this.health = health;
    }

    public String getHealth() {
        return health;
    }
}
