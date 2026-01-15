package org.example.builder;

import org.example.exceptions.ValidationException;
import org.example.mutable.AuthorMutable;
import org.example.model.Author;

import java.util.ArrayList;
import java.util.List;

public class AuthorBuilder {
    private final AuthorMutable mutable = new AuthorMutable();
    private final List<String> errors = new ArrayList<>();
    private final int lineCreated;

    public AuthorBuilder() {
        lineCreated = new Exception().getStackTrace()[1].getLineNumber();
    }

    public AuthorBuilder setName(String name) {
        if (name == null || name.isBlank()) {
            errors.add("Имя автора не может быть пустым (строка " + lineCreated + ")");
        } else {
            mutable.name = name;
        }
        return this;
    }

    // Высокоуровневая функция: копирование имени из другого автора
    public AuthorBuilder copyFrom(Author other) {
        if (other != null) {
            mutable.name = other.getName();
        }
        return this;
    }

    public Author build() {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return new Author(mutable.name);
    }
}
