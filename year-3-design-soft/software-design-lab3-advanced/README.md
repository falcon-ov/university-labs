# Лабораторная работа 3

- Тема: **Builder**.
- Выполнил: **Даниил Соколов**
- Группа: **I2302(ru)**

## Концепты

- Инициализация объекта в один этап
- Инициализация объекта поэтапно
- Immutable модели, их преимущества (избежание копий, гарантия неизменяемости)
  и недостатки (возможно выделение лишней памяти, изменение требует воссоздания)
- Mutable модели в контексте билдера
- Создание имплементации согласно интерфейсу
- Избежание инкапсуляции на уровне mutable моделей, инкапсуляция на уровне другого класса
- Инкапсуляция настройки и создания объекта, используя методы
- Использование паттерна scope для распределения настроек на несколько объектов
- Валидация объектов
- Идентификаторы объектов
- Использование интерфейсов для удаление повторяющегося кода
- Техника fluent builder
- Техника делегата конфигурации
- Абстрактные билдеры

## Задания

- Создайте группу взаимосвязанных классов объектов на вашу тематику.
  Должны существовать как минимум mutable модели для билдера,
  immutable модели создавайте, если они будут задуманы на выходе билдера.
  Разрешается использовать на выходе, например, XML файл вместо группы объектов,
  тогда отдельные immutable модели не обязательны.
    ```java
    //mutable
    package org.example.mutable;
    
    public class AuthorMutable {
        public String name;
    }
    ```
    ```java
    //immutable
    package org.example.model;
    
    public final class Author {
        private final String name;
    
        public Author(String name) {
            this.name = name;
        }
    
        public String getName() {
            return name;
        }
    }
    ```

- Создайте классы билдеров для всей группы классов.
  Через билдер возможно выставить каждое свойство объектов,
  а так же инициализировать их immutable версии, когда процесс инициализации завершен.
    ```java
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
    ```

- Добавьте код для валидации создаваемых объектов в функцию их создания.
  Добавьте базовую валидацию на уровне методов или свойств, инкапсулирующих поля mutable модели (на уровне модели, или на уровне билдера).
    ```java
    //...
    public AuthorBuilder setName(String name) {
    if (name == null || name.isBlank()) {
    errors.add("Имя автора не может быть пустым (строка " + lineCreated + ")");
    } else {
    mutable.name = name;
    }
    return this;
    }
    //...
    ```
- Используйте их в коде основной функции.

    ```java
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
                        .setAuthorAndPublisher(author, publisher) // высокоуровневая конфигурация
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
    ```


Задания (3 на выбор):

- Реализуйте более высокоуровневые функции конфигурации, которым необходим доступ к модели.

    ```java
    //...
    // Высокоуровневая функция: копирование имени из другого автора
    public AuthorBuilder copyFrom(Author other) {
        if (other != null) {
            mutable.name = other.getName();
        }
        return this;
    }
    //...
    ```

- Улучшите систему валидации, добавив указание строчки кода где был создан объект.
  Выводите ошибки для всех объектов с проблемами, а не только для первого.

    ```java
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
    ```
    ```java
    //...
    public AuthorBuilder setName(String name) {
        if (name == null || name.isBlank()) {
            errors.add("Имя автора не может быть пустым (строка " + lineCreated + ")");
        } else {
            mutable.name = name;
        }
        return this;
    }
    //...
    ```

- Создайте билдер под сохранение данных в базу данных.
  Его вывод могут быть или запросы SQL необходимые для сохранения объектов,
  или модели объектов для базы данных, с сохранением через ORM (типа EF Core).

    ```java
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
    ```
