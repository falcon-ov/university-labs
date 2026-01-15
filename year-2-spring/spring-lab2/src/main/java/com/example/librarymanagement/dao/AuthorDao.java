package com.example.librarymanagement.dao;

import com.example.librarymanagement.entity.Author;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public AuthorDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Author save(Author author) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(author);
            session.getTransaction().commit();
            return author;
        }
    }

    public Author findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Author.class, id);
        }
    }

    public List<Author> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Author", Author.class).list();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Author author = session.get(Author.class, id);
            if (author != null) {
                session.delete(author);
            }
            session.getTransaction().commit();
        }
    }
}