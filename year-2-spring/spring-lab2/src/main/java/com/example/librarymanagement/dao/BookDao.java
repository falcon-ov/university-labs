package com.example.librarymanagement.dao;

import com.example.librarymanagement.entity.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public BookDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Book save(Book book) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(book);
            session.getTransaction().commit();
            return book;
        }
    }

    public Book findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Book.class, id);
        }
    }

    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book", Book.class).list();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Book book = session.get(Book.class, id);
            if (book != null) {
                session.delete(book);
            }
            session.getTransaction().commit();
        }
    }
}