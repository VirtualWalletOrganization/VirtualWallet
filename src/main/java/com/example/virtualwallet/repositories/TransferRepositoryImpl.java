package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.repositories.contracts.TransferRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransferRepositoryImpl implements TransferRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransferRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Transfer> query = session.createQuery("FROM Transfer", Transfer.class);
            return query.getResultList();
        }
    }

    @Override
    public Transfer getTransferById(int transferId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transfer> query = session.createQuery(
                    "FROM Transfer as t where t.transferId = :transferId", Transfer.class);
            query.setParameter("transferId", transferId);
            List<Transfer> transfers = query.list();

            if (transfers.isEmpty()) {
                throw new EntityNotFoundException("Transfer", "id", String.valueOf(transferId));
            }

            return transfers.get(0);
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