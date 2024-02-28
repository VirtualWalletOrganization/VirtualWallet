package com.example.virtualwallet.services;

import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.repositories.contracts.ContactRepository;
import com.example.virtualwallet.services.contracts.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact getContactById(int contactId) {
        return contactRepository.getContactById(contactId);
    }

    @Override
    public List<Contact> getAllContactsByUserId(int userId) {
        return contactRepository.getAllContactsByUserId(userId);
    }

    @Override
    public void create(User currentUser, Contact newContact) {
        currentUser.getContacts().add(newContact);
        contactRepository.create(newContact);
    }

    @Override
    public void update(Contact contact) {
        contactRepository.update(contact);
    }

    @Override
    public void delete(int contactId) {
        Contact contact = getContactById(contactId);
        contactRepository.delete(contact);
    }
}