package com.example.librarymanagement.dao;

import com.example.librarymanagement.entity.Library;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LibraryDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public LibraryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Library save(Library library) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(library);
            session.getTransaction().commit();
            return library;
        }
    }

    public Library findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Library.class, id);
        }
    }

    public List<Library> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Library", Library.class).list();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Library library = session.get(Library.class, id);
            if (library != null) {
                session.delete(library);
            }
            session.getTransaction().commit();
        }
    }
}