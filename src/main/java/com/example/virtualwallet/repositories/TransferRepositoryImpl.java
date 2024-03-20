package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransferRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransferRepositoryImpl implements TransferRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransferRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<List<Transfer>> getAllTransfers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Transfer> query = session.createQuery("FROM Transfer", Transfer.class);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Transfer> getTransferById(int transferId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transfer> query = session.createQuery(
                    "FROM Transfer as t where t.transferId = :transferId", Transfer.class);
            query.setParameter("transferId", transferId);

            return Optional.ofNullable(query.list().get(0));
        }
    }

    @Override
    public Optional<List<Transfer>> getAllTransfersByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transfer> query = session.createQuery("select t From Transfer t WHERE t.receiver.id= :userId" +
                    " order by t.date", Transfer.class);
            query.setParameter("userId", userId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<Transfer>> getAllTransfersByStatus(Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transfer> query = session.createQuery(
                    "FROM Transfer as t where t.status = :status ", Transfer.class);
            query.setParameter("status", status);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Transfer create(Transfer transfer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(transfer);
            session.getTransaction().commit();
            return transfer;
        }
    }

    @Override
    public void update(Transfer transfer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(transfer);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Transfer transfer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(transfer);
            session.getTransaction().commit();
        }
    }
}