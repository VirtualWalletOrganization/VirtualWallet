package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class CardRepositoryImpl implements CardRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public CardRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Card> getAllCardsByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);

            if (user == null) {
                throw new EntityNotFoundException("User", "id", String.valueOf(userId));
            }

            List<Card> cards = session.createQuery("select c From Card c WHERE c.user.id= :userId ", Card.class)
                    .setParameter("userId", userId).list();

            return cards;
        }
    }

    @Override
    public Card getCardById(int cardId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("FROM Card as c where c.id = :id", Card.class);
            query.setParameter("id", cardId);
            List<Card> cards = query.list();


            if (cards.isEmpty()) {
                throw new EntityNotFoundException("Card", "id", String.valueOf(cardId));
            }

            return cards.get(0);
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