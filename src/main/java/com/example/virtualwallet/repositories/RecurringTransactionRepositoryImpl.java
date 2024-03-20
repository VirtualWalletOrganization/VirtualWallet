package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.repositories.contracts.RecurringTransactionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RecurringTransactionRepositoryImpl implements RecurringTransactionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public RecurringTransactionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<List<RecurringTransaction>> getAllRecurringTransactions() {
        try (Session session = sessionFactory.openSession()) {
            Query<RecurringTransaction> query = session.createQuery("FROM RecurringTransaction ",
                    RecurringTransaction.class);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<RecurringTransaction> getRecurringTransactionById(int transactionId) {
        try (Session session = sessionFactory.openSession()) {
            Query<RecurringTransaction> query = session.createQuery(
                    "FROM RecurringTransaction as t where t.transactionId = :transactionId", RecurringTransaction.class);
            query.setParameter("transactionId", transactionId);
            return Optional.ofNullable(query.list().get(0));
        }
    }

    @Override
    public Optional<List<RecurringTransaction>> getRecurringTransactionByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<RecurringTransaction> query = session.createQuery(
                    "FROM RecurringTransaction as t where t.userSender.id = :userId", RecurringTransaction.class);
            query.setParameter("userId", userId);

            List<RecurringTransaction> transactions = query.list();

            if (!transactions.isEmpty()) {
                return Optional.ofNullable(transactions);
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public RecurringTransaction create(RecurringTransaction recurringTransaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(recurringTransaction);
            session.getTransaction().commit();
            return recurringTransaction;
        }
    }

    @Override
    public void update(RecurringTransaction recurringTransaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(recurringTransaction);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(RecurringTransaction recurringTransaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(recurringTransaction);
            session.getTransaction().commit();
        }
    }
}