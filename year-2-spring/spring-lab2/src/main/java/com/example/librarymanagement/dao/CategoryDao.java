package com.example.librarymanagement.dao;

import com.example.librarymanagement.entity.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CategoryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Category save(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(category);
            session.getTransaction().commit();
            return category;
        }
    }

    public Category findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Category.class, id);
        }
    }

    public List<Category> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Category", Category.class).list();
        }
    }

    public List<Category> findAllByIds(List<Long> ids) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Category where id in :ids", Category.class)
                    .setParameter("ids", ids)
                    .list();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Category category = session.get(Category.class, id);
            if (category != null) {
                session.delete(category);
            }
            session.getTransaction().commit();
        }
    }
}