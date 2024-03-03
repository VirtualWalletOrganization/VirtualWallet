package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public WalletRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Wallet> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT w FROM Wallet w", Wallet.class)
                    .getResultList();
        }
    }

//    @Override
//    public Optional<List<User>> getAllUsersByWalletId(int walletId) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery(
//                    "SELECT u FROM User u JOIN u.wallets w WHERE w.id = :walletId", User.class);
//            query.setParameter("walletId", walletId);
//            return Optional.ofNullable(query.list());
//        }
//    }

//    @Override
//    public Optional<User> existsUserWithWallet(int userId, int walletId) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery(
//                    "SELECT u FROM User u JOIN u.wallets w WHERE u.id = :userId AND w.id = :walletId", User.class);
//            query.setParameter("userId", userId);
//            query.setParameter("walletId", walletId);
//            return Optional.ofNullable(query.uniqueResult());
//        }
//    }

    @Override
    public Optional<List<User>> getAllUsersByWalletId( int walletId) {
        try (Session session = sessionFactory.openSession()) {
            // "SELECT w from Wallet w JOIN w.cards c where c.id = :cardId", Wallet.class);
            Query<User> query = session.createQuery(
                    "SELECT u FROM User u JOIN u.wallets w WHERE w.id = :walletId", User.class);
            query.setParameter("walletId", walletId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Wallet> getWalletById(int walletId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery(
                    "FROM Wallet as w where w.id = :walletId", Wallet.class);
            query.setParameter("walletId", walletId);
            List<Wallet> wallets = query.list();

            return Optional.ofNullable(wallets.get(0));
        }
    }
    @Override
    public Optional<Wallet> getWalletByCardId(int cardId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery(
                    "SELECT w from Wallet w JOIN w.cards c where c.id = :cardId", Wallet.class);
            query.setParameter("cardId", cardId);
            List<Wallet> wallets = query.list();

            return Optional.ofNullable(wallets.get(0));
        }
    }

    @Override
    public Optional<Wallet> getDefaultWallet(int recipientUserId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery(
                    "FROM Wallet as w where w.creator.id = :recipientUserId" +
                            " AND w.isDefault=true", Wallet.class);
            query.setParameter("creatorId", recipientUserId);
            List<Wallet> wallets = query.list();

            return Optional.ofNullable(wallets.get(0));
        }
    }

    @Override
    public Optional<List<Wallet>> getByCreatorId(int creatorId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery(
                    "SELECT w FROM Wallet w WHERE w.creator.id = :creatorId", Wallet.class);
            query.setParameter("creatorId", creatorId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Wallet create(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(wallet);
            session.getTransaction().commit();
            return wallet;
        }
    }

    @Override
    public void update(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(wallet);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(wallet);
            session.getTransaction().commit();
        }
    }
}