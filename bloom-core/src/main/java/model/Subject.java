package model;

import static util.StringValidator.checkNullOrEmpty;

public class Subject {
    private final String subject;

    public Subject(String subject) {
        checkNullOrEmpty(subject, "subject");
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
