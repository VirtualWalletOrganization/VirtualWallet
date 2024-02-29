package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.User;

import java.util.List;

public interface ContactService {

    Contact getContactById(int contactId);

    List<Contact> getAllContactsByUserId(int userId);

    void create(User currentUser, Contact newContact);

    void update(Contact contact);

    void delete(int contactId);
}