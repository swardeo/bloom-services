package model.request;

import static util.StringValidator.checkNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NameRequest {

    private final String name;

    @JsonCreator
    public NameRequest(@JsonProperty("name") String name) {
        checkNullOrEmpty(name, "name");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
