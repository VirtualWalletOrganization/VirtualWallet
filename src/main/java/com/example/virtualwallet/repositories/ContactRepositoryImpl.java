package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.repositories.contracts.ContactRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContactRepositoryImpl implements ContactRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ContactRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Contact getContactById(int contactId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Contact> query = session.createQuery(
                    "FROM Contact as c where c.id = :contactId", Contact.class);
            query.setParameter("id", contactId);
            List<Contact> contacts = query.list();

            if (contacts.isEmpty()) {
                throw new EntityNotFoundException("Contact", "id", String.valueOf(contactId));
            }

            return contacts.get(0);
        }
    }

    @Override
    public List<Contact> getAllContactsByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Contact> query = session.createQuery(
                    "FROM Contact as c where user.id = :userId", Contact.class);
            query.setParameter("userId", userId);
            List<Contact> contacts = query.list();

            if (contacts.isEmpty()) {
                throw new EntityNotFoundException("Contacts");
            }

            return contacts;
        }
    }

    @Override
    public void create(Contact contact) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(contact);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Contact contact) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(contact);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Contact contact) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(contact);
            session.getTransaction().commit();
        }
    }
}