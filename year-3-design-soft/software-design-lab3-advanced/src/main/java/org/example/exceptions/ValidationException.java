package org.example.exceptions;


import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("Обнаружены ошибки: " + errors);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
