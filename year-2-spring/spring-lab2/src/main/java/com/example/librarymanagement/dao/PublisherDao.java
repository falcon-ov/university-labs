package com.example.librarymanagement.dao;

import com.example.librarymanagement.entity.Publisher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PublisherDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public PublisherDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Publisher save(Publisher publisher) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(publisher);
            session.getTransaction().commit();
            return publisher;
        }
    }

    public Publisher findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Publisher.class, id);
        }
    }

    public List<Publisher> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Publisher", Publisher.class).list();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Publisher publisher = session.get(Publisher.class, id);
            if (publisher != null) {
                session.delete(publisher);
            }
            session.getTransaction().commit();
        }
    }
}