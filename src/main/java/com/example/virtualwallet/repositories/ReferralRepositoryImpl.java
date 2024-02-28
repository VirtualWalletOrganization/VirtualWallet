package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.ReferralRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReferralRepositoryImpl implements ReferralRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ReferralRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Referral findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "FROM Referral as r where r.id = :id", Referral.class);
            query.setParameter("id", id);
            List<Referral> referrals = query.list();

            if (referrals.isEmpty()) {
                throw new EntityNotFoundException("Referral", "id", String.valueOf(id));
            }

            return referrals.get(0);
        }
    }

    @Override
    public String findReferralEmail(String referredEmail) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "SELECT r FROM Referral r WHERE referredEmail = :email", Referral.class);
            query.setParameter("referredEmail", referredEmail);
            Referral result = query.list().get(0);

            String email = result.getReferredEmail();

            if (email != null) {
                throw new DuplicateEntityException("Referral", "email", email);
            }

            return email;
        }
    }

    @Override
    public User findReferrerUserIdByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "SELECT user FROM Referral r WHERE r.referredEmail = :email", Referral.class);
            query.setParameter("referredEmail", email);
            Referral result = query.list().get(0);

            User user = result.getUser();

            if (user == null) {
                throw new EntityNotFoundException("User", "email", email);
            }

            return user;
        }
    }

    @Override
    public Status findReferralStatusByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Referral> query = session.createQuery(
                    "SELECT referralStatus FROM Referral r WHERE r.referredEmail = :email", Referral.class);
            query.setParameter("referredEmail", email);
            Referral result = query.list().get(0);

            Status status = result.getReferralStatus();

            if (status == null) {
                throw new EntityNotFoundException("Referrals", "email", email);
            }

            return status;
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