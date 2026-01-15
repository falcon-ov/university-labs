package org.example.builder;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Publisher;

public class DatabaseBuilder {

    public static String insertAuthor(Author author) {
        return String.format("INSERT INTO authors (name) VALUES ('%s');", author.getName());
    }

    public static String insertPublisher(Publisher publisher) {
        return String.format("INSERT INTO publishers (name) VALUES ('%s');", publisher.getName());
    }

    public static String insertBook(Book book) {
        return String.format(
                "INSERT INTO books (title, author, publisher) VALUES ('%s', '%s', '%s');",
                book.getTitle(),
                book.getAuthor().getName(),
                book.getPublisher().getName()
        );
    }
}
