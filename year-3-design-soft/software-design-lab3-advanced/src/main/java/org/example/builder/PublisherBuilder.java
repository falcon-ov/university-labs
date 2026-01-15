package org.example.builder;

import org.example.exceptions.ValidationException;
import org.example.mutable.PublisherMutable;
import org.example.model.Publisher;

import java.util.ArrayList;
import java.util.List;

public class PublisherBuilder {
    private final PublisherMutable mutable = new PublisherMutable();
    private final List<String> errors = new ArrayList<>();
    private final int lineCreated;

    public PublisherBuilder() {
        lineCreated = new Exception().getStackTrace()[1].getLineNumber();
    }

    public PublisherBuilder setName(String name) {
        if (name == null || name.isBlank()) {
            errors.add("Название издателя не может быть пустым (строка " + lineCreated + ")");
        } else {
            mutable.name = name;
        }
        return this;
    }

    public Publisher build() {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return new Publisher(mutable.name);
    }
}
