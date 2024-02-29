package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.User;

import java.util.List;

public interface ContactRepository {
    Contact getContactById(int contactId);

    List<Contact> getAllContactsByUserId(int userId);

    void create(Contact contact);

    void update(Contact contact);

    void delete(Contact contact);
}