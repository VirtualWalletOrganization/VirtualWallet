package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.utils.Pagination;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Page<Transaction> getAll(Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery("SELECT t FROM Transaction AS t", Transaction.class);
            return Pagination.pagedList(pageable, query.list());
        }
    }

    @Override
    public Optional<List<Transaction>> getAllTransactions() {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery("FROM Transaction", Transaction.class);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Transaction> getTransactionById(int transactionId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery(
                    "FROM Transaction as t where t.transactionId = :transactionId", Transaction.class);
            query.setParameter("transactionId", transactionId);

            return Optional.ofNullable(query.list().get(0));
        }
    }

    @Override
    public Optional<List<Transaction>> getAllTransactionsByWalletId(int walletId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery(
                    "SELECT t FROM Transaction t WHERE t.walletSender.id = :walletId" +
                            " or  t.walletReceiver.id= :walletId", Transaction.class);
            query.setParameter("walletId", walletId);
            List<Transaction> transactions = query.getResultList();

            List<Transaction> sortedTransactions = transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getDate))
                    .toList();

            return Optional.of(sortedTransactions);
        }
    }

    @Override
    public Optional<List<Transaction>> getAllTransactionsByStatus(Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery(
                    "FROM Transaction as t where t.transactionsStatus.transactionStatus = :status ", Transaction.class);
            query.setParameter("transactionsStatus", status);
            // query.setParameter("walletId", walletId);

            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Transaction create(Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(transaction);
            session.getTransaction().commit();
            return transaction;
        }
    }

    @Override
    public void update(Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(transaction);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(transaction);
            session.getTransaction().commit();
        }
    }
}