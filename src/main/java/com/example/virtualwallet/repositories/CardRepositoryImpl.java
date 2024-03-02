package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class CardRepositoryImpl implements CardRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CardRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Card> getAllCards() {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("SELECT c from Card c", Card.class);
            return query.list();
        }
    }

    @Override
    public Optional<List<Card>> getAllCardsByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("select c From Card c WHERE c.user.id= :userId ", Card.class);
            query.setParameter("userId", userId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Card> getCardById(int cardId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("FROM Card as c where c.id = :id", Card.class);
            query.setParameter("id", cardId);
            return Optional.ofNullable(query.uniqueResult());

        }
    }

    @Override
    public Optional<Card>getByCardNumber(String cardNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("SELECT c FROM Card AS c WHERE c.cardNumber = :cardNumber", Card.class);
            query.setParameter("cardNumber", cardNumber);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public void addCard(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(card);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateCard(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(card);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteCard(Card cardToDelete) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(cardToDelete);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Card> findExpiredCards(Date currentDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("FROM Card as c where c.expirationDate < :currentDate", Card.class);
            query.setParameter("currentDate", currentDate);
            return query.list();
        }
    }
}