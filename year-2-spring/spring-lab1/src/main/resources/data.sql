-- Добавление авторов
INSERT INTO AUTHOR (name) VALUES ('J.K. Rowling');
INSERT INTO AUTHOR (name) VALUES ('George R.R. Martin');
INSERT INTO AUTHOR (name) VALUES ('Agatha Christie');

-- Добавление издателей
INSERT INTO PUBLISHER (name) VALUES ('Bloomsbury');
INSERT INTO PUBLISHER (name) VALUES ('Bantam Books');
INSERT INTO PUBLISHER (name) VALUES ('HarperCollins');

-- Добавление категорий
INSERT INTO CATEGORY (name) VALUES ('Fantasy');
INSERT INTO CATEGORY (name) VALUES ('Mystery');
INSERT INTO CATEGORY (name) VALUES ('Adventure');

-- Добавление библиотеки
INSERT INTO LIBRARY (name) VALUES ('City Library');

-- Добавление книг
INSERT INTO BOOK (title, author_id, publisher_id, library_id)
VALUES ('Harry Potter and the Philosopher''s Stone', 1, 1, 1);

INSERT INTO BOOK (title, author_id, publisher_id, library_id)
VALUES ('A Game of Thrones', 2, 2, 1);

INSERT INTO BOOK (title, author_id, publisher_id, library_id)
VALUES ('Murder on the Orient Express', 3, 3, 1);

-- Связывание книг с категориями
INSERT INTO BOOK_CATEGORY (book_id, category_id) VALUES (1, 1); -- Harry Potter: Fantasy
INSERT INTO BOOK_CATEGORY (book_id, category_id) VALUES (1, 3); -- Harry Potter: Adventure
INSERT INTO BOOK_CATEGORY (book_id, category_id) VALUES (2, 1); -- A Game of Thrones: Fantasy
INSERT INTO BOOK_CATEGORY (book_id, category_id) VALUES (3, 2); -- Murder on the Orient Express: Mystery