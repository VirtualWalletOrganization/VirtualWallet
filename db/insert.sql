-- Insert data into photos_verifications
INSERT INTO photos_verifications (photo_card_id, selfie)
VALUES ('photo_card_1', 'selfie_1'),
       ('photo_card_2', 'selfie_2'),
       ('photo_card_3', 'selfie_3');

-- Insert data into identity_statuses
INSERT INTO identity_statuses (identity_verification_name)
VALUES ('APPROVED'),
       ('INCOMPLETE'),
       ('PENDING'),
       ('REJECTED');

-- Insert data into users_roles
INSERT INTO users_roles (user_role_name)
VALUES ('ADMIN'),
       ('USER');

-- Insert data into wallets_roles
INSERT INTO wallets_roles (wallet_role_name)
VALUES ('ADMIN'),
       ('USER');

-- Insert data into users
INSERT INTO users (first_name, last_name, username, password, email, phone_number, identity_status_id, role_id)
VALUES ('John', 'Doe', 'johndoe', 'password123', 'johndoe@example.com', '1234567890', 1, 1),
       ('Jane', 'Doe', 'janedoe', 'password456', 'janedoe@example.com', '0987654321', 2, 2);

-- Insert data into contacts
INSERT INTO contacts (user_id, username, phone_number)
VALUES (1, 'johndoe', '1234567890'),
       (2, 'janedoe', '0987654321');

-- Insert data into cards_types
INSERT INTO cards_types (card_type_name)
VALUES ('CREDIT'),
       ('DEBIT');

-- Insert data into cards
INSERT INTO cards (card_type_id, user_id, card_number, expiration_date, card_holder, check_number, currency, status)
VALUES (1, 1, '1234567890123456', '2024-12-31', 'John Doe', '123', 'USD', 'ACTIVE'),
       (2, 2, '9876543210987654', '2025-12-31', 'Jane Doe', '321', 'USD', 'ACTIVE');

-- Insert data into wallets_types
INSERT INTO wallets_types (wallet_type_name)
VALUES ('JOINT'),
       ('REGULAR');

-- Insert data into wallets
INSERT INTO wallets (creator_id, balance, currency, wallet_type_id)
VALUES (1, 1000.00, 'USD', 1),
       (2, 2000.00, 'USD', 2);

-- Insert data into cards_wallets
INSERT INTO cards_wallets (card_id, wallet_id)
VALUES (1, 1),
       (2, 2);

-- Insert data into users_wallets
INSERT INTO users_wallets (user_id, wallet_id)
VALUES (1, 1),
       (2, 2);

-- Insert data into transactions_statuses
INSERT INTO transactions_statuses (status_name)
VALUES ('COMPLETED'),
       ('FAILED'),
       ('PENDING');

-- Insert data into transactions_types
INSERT INTO transactions_types (transaction_type_name)
VALUES ('SINGLE'),
       ('RECURRING');

-- Insert data into transactions
INSERT INTO transactions (sender_wallet_id, receiver_wallet_id, amount, currency, direction, date, transaction_status_id, description, transaction_type_id)
VALUES (1, 2, 500.00, 'USD', 'OUTGOING', '2024-03-01', 1, 'Payment for services', 1),
       (2, 1, 1000.00, 'USD', 'INCOMING', '2024-03-01', 1, 'Salary', 1);

-- Insert data into recurring_transactions
INSERT INTO recurring_transactions (transaction_id, intervals, start_date, end_date)
VALUES (1, 'MONTHLY', '2024-03-01', '2024-12-31'),
       (2, 'MONTHLY', '2024-03-01', '2024-12-31');

-- Insert data into referrals
INSERT INTO referrals (user_id, referred_email, referral_status, bonus)
VALUES (1, 'janedoe@example.com', 'PENDING', 50.00),
       (2, 'johndoe@example.com', 'PENDING', 50.00);

-- Insert data into overdrafts_types
INSERT INTO overdrafts_types (overdraft_name, overdraft_limit, overdraft_interest, duration)
VALUES ('Overdraft Type 1', 1000.00, 10, '2024-03-01'),
       ('Overdraft Type 2', 2000.00, 20, '2024-03-01');

-- Insert data into overdrafts
INSERT INTO overdrafts (wallet_id, overdraft_type_id, start_date, due_date)
VALUES (1, 1, '2024-03-01', '2024-12-31'),
       (2, 2, '2024-03-01', '2024-12-31');

-- Insert data into savings_types
INSERT INTO savings_types (saving_name, saving_amount, saving_interest)
VALUES ('Saving Type 1', 1000.00, 10),
       ('Saving Type 2', 2000.00, 20);

-- Insert data into savings
INSERT INTO savings (wallet_id, saving_type_id, start_date, due_date)
VALUES (1, 1, '2024-03-01', '2024-12-31'),
       (2, 2, '2024-03-01', '2024-12-31');

-- Insert data into spending_categories
INSERT INTO spending_categories (category_name)
VALUES ('Category 1'),
       ('Category 2');

-- Insert data into transfers
INSERT INTO transfers (wallet_id, card_id, amount, currency, direction, date, status, category_id)
VALUES (1, 1, 500.00, 'USD', 'OUTGOING', '2024-03-01', 'COMPLETED', 1),
       (2, 2, 1000.00, 'USD', 'INCOMING', '2024-03-01', 'COMPLETED', 2);