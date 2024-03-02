package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.utils.UserFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAll(UserFilterOptions userFilterOptions) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();


            userFilterOptions.getUsername().ifPresent(value -> {
                filters.add("username like :username");
                params.put("username", String.format("%%%s%%", value));
            });

            userFilterOptions.getFirstName().ifPresent(value -> {
                filters.add(" firstName like :firstName ");
                params.put("firstName", String.format("%%%s%%", value));
            });

            userFilterOptions.getLastName().ifPresent(value -> {
                filters.add(" lastName like :lastName ");
                params.put("lastName", String.format("%%%s%%", value));
            });

            userFilterOptions.getEmail().ifPresent(value -> {
                filters.add(" email like :email ");
                params.put("email", String.format("%%%s%%", value));
            });

            userFilterOptions.getRole().ifPresent(value -> {
                filters.add(" role = :role ");
                params.put("role", value);
            });
            userFilterOptions.getStatus().ifPresent(value -> {
                filters.add(" status = :status ");
                params.put("status", value);
            });

            StringBuilder queryString = new StringBuilder("from User");

            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(userFilterOptions));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);

            return query.list();
        }
    }

    @Override
    public long getAllNumber() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list().size();

        }
    }

    @Override
    public Optional<User> getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);

            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<User> getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            return Optional.ofNullable(query.list().get(0));

        }
    }

    @Override
    public Optional<User> getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);

            return Optional.ofNullable(query.list().get(0));
        }
    }

    public Optional<User>getByPhoneNumber(String phoneNumber){
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where phoneNumber = :phoneNumber", User.class);
            query.setParameter("phoneNumber", phoneNumber);

            return Optional.ofNullable(query.list().get(0));
        }
    }

    @Override
    public void registerUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateUser(User targetUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(targetUser);
            session.getTransaction().commit();
        }
    }

    @Override
    public void reactivated(User targetUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            targetUser.setDeleted(false);
            session.merge(targetUser);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteUser(User userToDelete) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            userToDelete.setDeleted(true);
            session.merge(userToDelete);
            session.getTransaction().commit();
        }
    }

    public boolean isDataBaseEmpty() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u", Long.class);
            Long userCount = query.uniqueResult();

            return userCount == 0;
        }
    }

    public boolean existsByPhoneNumber(User userPhoneNumberToBeUpdate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User  u WHERE u.phoneNumber = :phoneNumber", Long.class);
            query.setParameter("phoneNumber", userPhoneNumberToBeUpdate.getPhoneNumber());
            Long userCount = query.uniqueResult();

            return userCount != null && userCount > 0;
        }
    }

    private String generateOrderBy(UserFilterOptions userFilterOptions) {
        if (userFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (userFilterOptions.getSortBy().get()) {
            case "username":
                orderBy = "username";
                break;
            case "email":
                orderBy = "email";
                break;
            case "role":
                orderBy = "phoneNumber";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (userFilterOptions.getSortOrder().isPresent() && userFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}