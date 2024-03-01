package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.repositories.contracts.SpendingCategoryRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpendingCategoryRepositoryImpl implements SpendingCategoryRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public SpendingCategoryRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<SpendingCategory> getAllSpendingCategories() {
        try (Session session = sessionFactory.openSession()) {
            Query<SpendingCategory> query = session.createQuery("FROM SpendingCategory", SpendingCategory.class);
            return query.getResultList();
        }
    }

    @Override
    public SpendingCategory getSpendingCategoryById(int categoryId) {
        try (Session session = sessionFactory.openSession()) {
            SpendingCategory category = session.get(SpendingCategory.class, categoryId);
            if (category == null) {
                throw new EntityNotFoundException("SpendingCategory", "id", String.valueOf(categoryId));
            }
            return category;
        }
    }

    @Override
    public SpendingCategory create(SpendingCategory category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(category);
            session.getTransaction().commit();
            return category;
        }
    }

    @Override
    public void update(SpendingCategory category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(category);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(SpendingCategory category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(category);
            session.getTransaction().commit();
        }
    }
}