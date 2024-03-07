-- Insert data into photos_verifications table
INSERT INTO photos_verifications (creator_id, photo_card_id, selfie)
VALUES (1, 'card123.jpg', 'selfie123.jpg');

-- Insert data into identity_statuses table
INSERT INTO identity_statuses (identity_verification_name)
VALUES ('APPROVED'),
       ('INCOMPLETE'),
       ('PENDING'),
       ('REJECTED');

-- Insert data into users_roles table
INSERT INTO users_roles (user_role_name)
VALUES ('ADMIN'),
       ('USER');

-- Insert data into wallets_roles table
INSERT INTO wallets_roles (wallet_role_name)
VALUES ('ADMIN'),
       ('USER');

-- Insert data into users table
INSERT INTO users (first_name, last_name, username, password, email, phone_number, identity_status_id, role_id, status,
                   wallet_role_id)
VALUES ('John', 'Doe', 'john_doe', 'password123', 'john@example.com', '1234567890', 2, 1, 'ACTIVE', 1),
       ('Kris', 'Kris', 'kris', 'pass1', 'kris@example.com', '1234567894', 1, 1, 'ACTIVE', 2);

-- Insert data into cards_types table
INSERT INTO cards_types (card_type_name)
VALUES ('CREDIT'),
       ('DEBIT');

-- Insert data into wallets_types table
INSERT INTO wallets_types (wallet_type_name)
VALUES ('JOINT'),
       ('REGULAR');

INSERT INTO wallets (creator_id, balance, currency, wallet_type_id, is_default)
VALUES (1, 1000.00, 'USD', 1, true),
       (2, 2000.00, 'USD', 2, true);

-- Insert data into spending_categories table
INSERT INTO spending_categories (category_name)
VALUES ('Food'),
       ('Shopping'),
       ('Travel');

-- Insert data into transactions_statuses table
INSERT INTO transactions_statuses (status_name)
VALUES ('COMPLETED'),
       ('FAILED'),
       ('PENDING'),
       ('PENDING_RECURRING_REQUEST'),
       ('REJECT');

-- Insert data into transactions_types table
INSERT INTO transactions_types (transaction_type_name)
VALUES ('SINGLE'),
       ('RECURRING');

INSERT INTO transactions (sender_wallet_id, receiver_wallet_id, amount, currency, date,
                          transaction_status_id,
                          description, transaction_type_id)
VALUES (1, 1, 100.00, 'USD', '2024-03-04 00:00:00', 1, 'Payment for goods', 2),
       (2, 2, 50.00, 'EUR', '2024-03-04 00:00:00', 2, 'Refund for returned item', 2);

insert into users_wallets (user_id, wallet_id)
values (1, 1),
       (2, 2);