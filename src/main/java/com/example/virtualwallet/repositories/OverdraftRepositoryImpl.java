package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Overdraft;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.repositories.contracts.OverdraftRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OverdraftRepositoryImpl implements OverdraftRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public OverdraftRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Overdraft> getAllOverdrafts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Overdraft> query = session.createQuery("FROM Overdraft", Overdraft.class);
            return query.getResultList();
        }
    }

    @Override
    public Overdraft getOverdraftById(int overdraftId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Overdraft> query = session.createQuery(
                    "FROM Overdraft as o where o.id = :overdraftId", Overdraft.class);
            query.setParameter("overdraftId", overdraftId);
            List<Overdraft> overdrafts = query.list();

            if (overdrafts.isEmpty()) {
                throw new EntityNotFoundException("Overdraft", "id", String.valueOf(overdraftId));
            }

            return overdrafts.get(0);
        }
    }

    @Override
    public Overdraft create(Overdraft overdraft) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(overdraft);
            session.getTransaction().commit();
            return overdraft;
        }
    }

    @Override
    public void update(Overdraft overdraft) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(overdraft);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Overdraft overdraft) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(overdraft);
            session.getTransaction().commit();
        }
    }

    @Override
    public void enableOverdraft(int userId, boolean enable) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<Wallet> query = session.createQuery(
                    "FROM Wallet as w where w.creator.id = :userId", Wallet.class);
            query.setParameter("userId", userId);
            List<Wallet> wallets = query.list();

            for (Wallet wallet : wallets) {
                wallet.setOverdraftEnabled(enable);
                session.merge(wallet);
            }

            session.getTransaction().commit();
        }
    }

    @Override
    public void updatePaidStatus(int overdraftId, boolean isPaid) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Overdraft overdraft = session.get(Overdraft.class, overdraftId);
            overdraft.setPaid(isPaid);
            session.getTransaction().commit();
        }
    }
}