package org.example.builder;

import org.example.exceptions.ValidationException;
import org.example.mutable.BookMutable;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Publisher;

import java.util.ArrayList;
import java.util.List;

public class BookBuilder {
    private final BookMutable mutable = new BookMutable();
    private final List<String> errors = new ArrayList<>();
    private final int lineCreated;

    public BookBuilder() {
        lineCreated = new Exception().getStackTrace()[1].getLineNumber();
    }

    public BookBuilder setTitle(String title) {
        if (title == null || title.isBlank()) {
            errors.add("Название книги не может быть пустым (строка " + lineCreated + ")");
        } else {
            mutable.title = title;
        }
        return this;
    }

    public BookBuilder setAuthor(Author author) {
        if (author == null) {
            errors.add("Автор не может быть null (строка " + lineCreated + ")");
        } else {
            mutable.author = new org.example.mutable.AuthorMutable();
            mutable.author.name = author.getName();
        }
        return this;
    }

    public BookBuilder setPublisher(Publisher publisher) {
        if (publisher == null) {
            errors.add("Издатель не может быть null (строка " + lineCreated + ")");
        } else {
            mutable.publisher = new org.example.mutable.PublisherMutable();
            mutable.publisher.name = publisher.getName();
        }
        return this;
    }

    // Высокоуровневый метод: задать автора и издателя сразу
    public BookBuilder setAuthorAndPublisher(Author author, Publisher publisher) {
        setAuthor(author);
        setPublisher(publisher);
        return this;
    }

    public Book build() {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        Author author = new Author(mutable.author.name);
        Publisher publisher = new Publisher(mutable.publisher.name);
        return new Book(mutable.title, author, publisher);
    }
}
