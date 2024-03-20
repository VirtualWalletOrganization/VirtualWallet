package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactRepository {

    Optional<Contact> getContactById(int contactId);

    Optional<List<Contact>> getAllContactsByUserId(int userId);

    void create(Contact contact);

    void update(Contact contact);

    void delete(Contact contact);
}