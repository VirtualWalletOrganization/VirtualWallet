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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

//    @Override
//    public Page<Transaction> filter(Date startDate, Date endDate, Optional<User> sender, Optional<User> recipient,
//                                    Sort sortBy, Pageable pageable, boolean includeUnverified) {
//        Timestamp startTimestamp = new Timestamp(startDate.getTime());
//        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
//        try (Session session = sessionFactory.openSession()) {
//            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
//            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
//            List<Predicate> predicateList = new ArrayList<>();
//            sender.ifPresent((User actualSender) ->
//                    predicateList.add(criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), actualSender)));
//            recipient.ifPresent((User actualRecipient) ->
//                    predicateList.add(criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), actualRecipient)));
//            if (!includeUnverified) {
//                predicateList.add(criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED));
//            }
//            Predicate[] predicates = new Predicate[]{
//                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
//                    criteriaBuilder.and(predicateList.toArray(new Predicate[0]))
//            };
//            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
//            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
//            Query<Transaction> query = session.createQuery(criteriaQuery);
//            Pagination.addPagination(pageable, query);
//            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
//            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
//        }
//    }
//
//    @Override
//    public Page<Transaction> filterOutgoing(Date startDate, Date endDate, User sender, Optional<User> recipient,
//                                            Sort sortBy, Pageable pageable, boolean includeUnverified) {
//        Timestamp startTimestamp = new Timestamp(startDate.getTime());
//        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
//        try (Session session = sessionFactory.openSession()) {
//            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
//            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
//            List<Predicate> predicateList = new ArrayList<>();
//            recipient.ifPresent((User actualRecipient) ->
//                    predicateList.add(criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), actualRecipient)));
//            if (!includeUnverified) {
//                predicateList.add(criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED));
//            }
//            Predicate[] predicates = new Predicate[]{
//                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
//                    criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.CARD_TO_WALLET),
//                    criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), sender),
//                    criteriaBuilder.and(predicateList.toArray(new Predicate[0]))
//            };
//            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
//            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
//            Query<Transaction> query = session.createQuery(criteriaQuery);
//            Pagination.addPagination(pageable, query);
//            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
//            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
//        }
//    }
//
//    @Override
//    public Page<Transaction> filterForUser(Date startDate, Date endDate, User user, Sort sortBy, Pageable pageable) {
//        Timestamp startTimestamp = new Timestamp(startDate.getTime());
//        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
//        try (Session session = sessionFactory.openSession()) {
//            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
//            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
//            Predicate userIsSender = criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), user);
//            Predicate userIsRecipient = criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), user);
//            Predicate[] predicates = new Predicate[] {
//                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
//                    criteriaBuilder.or(userIsSender, userIsRecipient),
//                    criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED)
//            };
//            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
//            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
//            Query<Transaction> query = session.createQuery(criteriaQuery);
//            Pagination.addPagination(pageable, query);
//            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
//            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
//        }
//    }
//
//    @Override
//    public Page<Transaction> filterForUserWithCounterparty(Date startDate, Date endDate, User user, User otherUser, Sort sortBy, Pageable pageable) {
//        Timestamp startTimestamp = new Timestamp(startDate.getTime());
//        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
//        try (Session session = sessionFactory.openSession()) {
//            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
//            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
//            Predicate userIsSender = criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), user);
//            Predicate userIsRecipient = criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), user);
//            Predicate otherUserIsSender = criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), otherUser);
//            Predicate otherUserIsRecipient = criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), otherUser);
//            Predicate[] predicates = new Predicate[]{
//                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
//                    criteriaBuilder.or(criteriaBuilder.and(userIsSender, otherUserIsRecipient),
//                            criteriaBuilder.and(otherUserIsSender, userIsRecipient)),
//                    criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED)
//            };
//            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
//            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
//            Query<Transaction> query = session.createQuery(criteriaQuery);
//            Pagination.addPagination(pageable, query);
//            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
//            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
//        }
//    }
}