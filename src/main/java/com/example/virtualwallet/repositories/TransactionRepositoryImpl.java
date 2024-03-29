package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import com.example.virtualwallet.utils.TransactionHistoryFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<List<Transaction>> getAllTransactions(TransactionFilterOptions transactionFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            transactionFilterOptions.getSender().ifPresent(value -> {
                filters.add("userSender.username like :userSender");
                params.put("userSender", String.format("%%%s%%", value));
            });

            transactionFilterOptions.getRecipient().ifPresent(value -> {
                filters.add("userReceiver.username like :userReceiver");
                params.put("userReceiver", String.format("%%%s%%", value));
            });

            transactionFilterOptions.getAmount().ifPresent(value -> {
                filters.add("amount >= :amount");
                params.put("amount", value);
            });

            transactionFilterOptions.getCurrency().ifPresent(value -> {
                filters.add("currency = :currency");
                params.put("currency", value);
            });

            transactionFilterOptions.getCreationTime().ifPresent(value -> {
                filters.add("date > :date");
                params.put("date", value);
            });

            StringBuilder queryString = new StringBuilder("from Transaction");

            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(transactionFilterOptions));

            Query<Transaction> query = session.createQuery(queryString.toString(), Transaction.class);
            query.setProperties(params);


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
    public Optional<List<Transaction>> getAllTransactionsByUserId(int userId,
                                                                  TransactionHistoryFilterOptions
                                                                          transactionHistoryFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            String baseQuery = "FROM Transaction as t WHERE t.userSender.id = :userId OR t.userReceiver.id = :userId";

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            StringBuilder queryString = new StringBuilder(baseQuery);
            params.put("userId", userId);

            transactionHistoryFilterOptions.getStartDate().ifPresent(value -> {
                filters.add("t.date >= :startDate");
                params.put("startDate", value);
            });
            transactionHistoryFilterOptions.getEndDate().ifPresent(value -> {
                filters.add("t.date <= :endDate");
                params.put("endDate", value);
            });
            transactionHistoryFilterOptions.getCounterparty().ifPresent(value -> {
                filters.add("(t.userSender.username = :counterparty OR t.userReceiver.username = :counterparty)");
                params.put("counterparty", value);
            });

            if (!filters.isEmpty()) {
                queryString.append(" AND ").append(String.join(" AND ", filters));
            }

            queryString.append(generateOrderByUserId(transactionHistoryFilterOptions));

            Query<Transaction> query = session.createQuery(queryString.toString(), Transaction.class);
            query.setProperties(params);

            return Optional.ofNullable(query.list());
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
    public Optional<List<Transaction>> getAllTransactionsByStatus(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery(
                    "FROM Transaction as t WHERE (t.transactionsStatus.id = :statusId" +
                            " or t.transactionsStatus.id= :statusId2) and t.userSender.id = :userId " +
                            "ORDER BY t.date", Transaction.class);
            query.setParameter("statusId", "4");
            query.setParameter("statusId2", "1");
            query.setParameter("userId", user.getId());

            List<Transaction> transactions = query.list();
            if (!transactions.isEmpty()) {
                return Optional.of(transactions);
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<List<Transaction>> getAllTransactionsByTransactionType(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery(
                    "FROM Transaction as t WHERE t.transactionsType.id = :typeId " +
                            "and t.userSender.id = :userId " +
                            "ORDER BY t.date", Transaction.class);
            query.setParameter("typeId", "2");
            query.setParameter("userId", user.getId());

            List<Transaction> transactions = query.list();
            if (!transactions.isEmpty()) {
                return Optional.of(transactions);
            } else {
                return Optional.empty();
            }
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

    private String generateOrderBy(TransactionFilterOptions transactionFilterOptions) {
        if (transactionFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (transactionFilterOptions.getSortBy().get()) {
            case "userSender":
                orderBy = "userSender.username";
                break;
            case "userReceiver":
                orderBy = "userReceiver.username";
                break;
            case "amount":
                orderBy = "amount";
                break;
            case "date":
                orderBy = "date";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (transactionFilterOptions.getSortOrder().isPresent()
                && transactionFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }

    private String generateOrderByUserId(TransactionHistoryFilterOptions transactionHistoryFilterOptions) {
        if (transactionHistoryFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (transactionHistoryFilterOptions.getSortBy().get()) {
            case "startDate":
                orderBy = "t.date";
                break;
            case "endDate":
                orderBy = "t.date";
                break;
            case "counterparty":
                orderBy = "CASE WHEN t.userSender.id = :userId THEN t.userSender.username ELSE t.userReceiver.username END";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (transactionHistoryFilterOptions.getSortOrder().isPresent()
                && transactionHistoryFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}