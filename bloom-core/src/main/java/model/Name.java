package model;

import static util.StringValidator.checkNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonValue;

public class Name {

    @JsonValue private final String name;

    public Name(String name) {
        checkNullOrEmpty(name, "name");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
