package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.ReferralRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReferralRepositoryImpl implements ReferralRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ReferralRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Referral> getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "FROM Referral as r where r.id = :id", Referral.class);
            query.setParameter("id", id);

            return Optional.ofNullable(query.list().get(0));
        }
    }

    @Override
    public Optional<String> getReferralEmail(String referredEmail) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "SELECT r FROM Referral r WHERE referredEmail = :email", Referral.class);
            query.setParameter("referredEmail", referredEmail);
            Referral result = query.list().get(0);
            String email = result.getReferredEmail();

            return Optional.ofNullable(email);
        }
    }

    @Override
    public Optional<User> getReferrerUserIdByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "SELECT user FROM Referral r WHERE r.referredEmail = :email", Referral.class);
            query.setParameter("referredEmail", email);
            Referral result = query.list().get(0);

            return Optional.ofNullable(result.getUser());
        }
    }

    @Override
    public Optional<Status> getReferralStatusByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "SELECT referralStatus FROM Referral r WHERE r.referredEmail = :email", Referral.class);
            query.setParameter("referredEmail", email);
            Referral result = query.list().get(0);

            return Optional.ofNullable(result.getReferralStatus());
        }
    }

    @Override
    public Referral create(Referral referral) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(referral);
            session.getTransaction().commit();
            return referral;
        }
    }
}