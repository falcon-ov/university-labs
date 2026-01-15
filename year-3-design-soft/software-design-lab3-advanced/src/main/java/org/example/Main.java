package org.example;

import org.example.builder.*;
import org.example.exceptions.ValidationException;
import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        try {
            Author author = new AuthorBuilder()
                    .setName("Фёдор Достоевский")
                    .build();

            Publisher publisher = new PublisherBuilder()
                    .setName("Русский классик")
                    .build();

            Book book = new BookBuilder()
                    .setTitle("Преступление и наказание")
                    .setAuthorAndPublisher(author, publisher) // 🔹 высокоуровневая конфигурация
                    .build();

            // SQL запросы
            System.out.println(DatabaseBuilder.insertAuthor(author));
            System.out.println(DatabaseBuilder.insertPublisher(publisher));
            System.out.println(DatabaseBuilder.insertBook(book));

        } catch (ValidationException e) {
            System.out.println("Ошибки при создании объекта:");
            e.getErrors().forEach(System.out::println);
        }
    }
}
