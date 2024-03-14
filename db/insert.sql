
INSERT INTO photos_verifications (creator_id, photo_card_id, selfie) VALUES
                                                                         (1, 'photo123', 'selfie123'),
                                                                         (2, 'photo456', 'selfie456'),
                                                                         (3, 'photo789', 'selfie789');

-- Example data for identity_statuses
INSERT INTO identity_statuses (identity_verification_name) VALUES
                                                               ('APPROVED'),
                                                               ('INCOMPLETE'),
                                                               ('PENDING'),
                                                               ('REJECTED');

-- Example data for users_roles
INSERT INTO users_roles (user_role_name) VALUES
                                             ('ADMIN'),
                                             ('USER');

-- Example data for wallets_roles
INSERT INTO wallets_roles (wallet_role_name) VALUES
                                                 ('ADMIN'),
                                                 ('USER');

-- Example data for users
INSERT INTO users (first_name, last_name, username, password, email, phone_number, identity_status_id, role_id)
VALUES
    ('Kris', 'Adams', 'kris', 'pass1', 'kris.adams@example.com', '1234567890', 3, 2),
    ('Alice', 'Johnson', 'alice', 'pass2', 'alice@example.com', '1112223333', 3, 2),
    ('Bob', 'Williams', 'bob', 'pass3', 'bob@example.com', '4445556666', 3, 2),
    ('Tom', 'Ford', 'tom', 'pass4', 'tom@example.com', '8885556666', 3, 2),
    ('Eve', 'Brown', 'eve', 'pass5', 'eve@example.com', '7778889999', 2, 2);

-- Example data for contacts
INSERT INTO contacts (user_id, username, phone_number) VALUES
                                                           (1, 'jane_smith', '0987654321'),
                                                           (2, 'john_doe', '1234567890');

-- Example data for cards_types
INSERT INTO cards_types (card_type_name) VALUES
                                             ('CREDIT'),
                                             ('DEBIT');

-- Example data for cards
INSERT INTO cards (cards.card_id,card_type_id, user_id, card_number, expiration_date, card_holder, check_number, currency, status)
VALUES
    (1,2, 1, '1234567812345678', '2025-12-31', 'Kris Adams', '123', 'BGN', 'ACTIVE'),
    (2,2, 1, '9874567812345678', '2024-12-31', 'Kris Adams', '789', 'BGN', 'ACTIVE'),
    (3,2, 2, '5555557812345678', '2025-12-31', 'Alice Johnson', '567', 'BGN', 'ACTIVE'),
    (4,2, 3, '7774567812999678', '2025-09-30', 'Bob Williams', '345', 'BGN', 'ACTIVE'),
    (5,2, 4, '5678567812345678', '2024-06-30', 'Tom Ford', '098', 'BGN', 'ACTIVE'),
    (6,2, 5, '5555543298765432', '2025-12-31', 'Eve Brown', '456', 'BGN', 'ACTIVE');

-- Example data for wallets_types
INSERT INTO wallets_types (wallet_type_name) VALUES
                                                 ('JOINT'),
                                                 ('REGULAR');
INSERT INTO wallets (wallet_id, creator_id, balance, currency, wallet_type_id, is_default, is_deleted, overdraft_enabled, saving_enabled)
VALUES
    (1, 1, 500.00, 'BGN', 2, true, false, true, false),
    (2, 1, 1000.00, 'BGN', 1, false, false, false, true),
    (3, 2, 1000.00, 'BGN', 2, false, false, false, true),
    (4, 3, 1000.00, 'BGN', 2, false, false, false, true),
    (5, 4, 1000.00, 'BGN', 2, false, false, false, true),
    (6, 5, 1000.00, 'BGN', 2, false, false, false, true),
    (7, 5, 0.00, 'USD', 2, false, false, false, false);



-- Example data for cards_wallets (linking cards to wallets)
INSERT INTO cards_wallets (card_id, wallet_id) VALUES
                                                   (1, 1),
                                                   (2, 2),
                                                   (3, 3),
                                                   (4, 4),
                                                   (5, 5),
                                                   (6, 6);

-- Example data for users_wallets (linking users to wallets)
INSERT INTO users_wallets (user_id, wallet_id) VALUES
                                                   (1, 1),
                                                   (1, 2),
                                                   (2, 3),
                                                   (3, 4),
                                                   (4, 5),
                                                   (5, 6),
                                                   (5, 7);

-- Example data for transactions_statuses
INSERT INTO transactions_statuses (status_name) VALUES
                                                    ('EXPIRED'),
                                                    ('COMPLETED'),
                                                    ('FAILED'),
                                                    ('PENDING'),
                                                    ('PENDING_RECURRING_REQUEST'),
                                                    ('REJECT');

-- Example data for transactions_types
INSERT INTO transactions_types (transaction_type_name) VALUES
                                                           ('DUMMY'),
                                                           ('SINGLE'),
                                                           ('RECURRING');

-- Example data for transactions
INSERT INTO transactions (sender_wallet_id, receiver_wallet_id, amount, currency, date, transaction_status_id, description, transaction_type_id)
VALUES
    (1, 3, 200.00, 'USD', '2024-03-11 12:30:00', 2, 'Payment for services', 2),
    (2, 4, 200.00, 'USD', '2024-03-11 12:30:00', 2, 'Reimbursement for dinner', 2),
    (3, 5, 200.00, 'USD', '2024-03-12 12:30:00', 2, 'Payment for services', 2),
    (4, 6, 50.00, 'EUR', '2024-03-13 13:45:00', 2, 'Reimbursement for dinner', 2);

-- Example data for recurring_transactions
INSERT INTO recurring_transactions (transaction_id, intervals, start_date, end_date)
VALUES
    (1, 'MONTHLY', '2024-03-13', '2024-12-01'),
    (3, 'MONTHLY', '2024-04-01', '2024-12-01');

-- Example data for referrals
INSERT INTO referrals (user_id, referred_email, referral_status, bonus) VALUES
                                                                            (1, 'friend1@example.com', 'PENDING', 10.00),
                                                                            (2, 'friend2@example.com', 'COMPLETED', 20.00);

-- Example data for overdrafts_types
INSERT INTO overdrafts_types (overdraft_name, overdraft_limit, overdraft_interest, duration)
VALUES
    ('Standard', 500.00, 10, '2024-03-11');

-- Example data for overdrafts
INSERT INTO overdrafts (wallet_id, overdraft_type_id, start_date, due_date, is_paid)
VALUES
    (1, 1, '2024-03-11', '2024-03-31', false),
    (2, 1, '2024-03-11', '2024-03-31', true);

-- Example data for savings_types
INSERT INTO savings_types (saving_name, saving_amount, saving_interest)
VALUES
    ('Regular', 1000.00, 5),
    ('High-Yield', 5000.00, 10);

-- Example data for savings
INSERT INTO savings (wallet_id, saving_type_id, start_date, due_date)
VALUES
    (1, 1, '2024-03-11', '2025-03-11'),
    (2, 2, '2024-03-11', '20
-- Example data for wallets_types
INSERT INTO wallets_types (wallet_type_name) VALUES
                                                 (''JOINT''),
                                                 (''REGULAR'');
INSERT INTO wallets (wallet_id, creator_id, balance, currency, wallet_type_id, is_default, is_deleted, overdraft_enabled, saving_enabled)
VALUES
    (1, 1, 500.00, ''BGN'', 2, true, false, true, false),
    (2, 1, 1000.00, ''BGN'', 1, false, false, false, true),
    (3, 2, 1000.00, ''BGN'', 2, false, false, false, true),
    (4, 3, 1000.00, ''BGN'', 2, false, false, false, true),
    (5, 4, 1000.00, ''BGN'', 2, false, false, false, true),
    (6, 5, 1000.00, ''BGN'', 2, false, false, false, true),
    (7, 5, 0.00, ''USD'', 2, false, false, false, false);



-- Example data for cards_wallets (linking cards to wallets)
INSERT INTO cards_wallets (card_id, wallet_id) VALUES
                                                   (1, 1),
                                                   (2, 2),
                                                   (3, 3),
                                                   (4, 4),
                                                   (5, 5),
                                                   (6, 6);

-- Example data for users_wallets (linking users to wallets)
INSERT INTO users_wallets (user_id, wallet_id) VALUES
                                                   (1, 1),
                                                   (1, 2),
                                                   (2, 3),
                                                   (3, 4),
                                                   (4, 5),
                                                   (5, 6),
                                                   (5, 7);

-- Example data for transactions_statuses
INSERT INTO transactions_statuses (status_name) VALUES
                                                    (''EXPIRED''),
                                                    (''COMPLETED''),
                                                    (''FAILED''),
                                                    (''PENDING''),
                                                    (''PENDING_RECURRING_REQUEST''),
                                                    (''REJECT'');

-- Example data for transactions_types
INSERT INTO transactions_types (transaction_type_name) VALUES
                                                           (''DUMMY''),
                                                           (''SINGLE''),
                                                           (''RECURRING'');

-- Example data for transactions
INSERT INTO transactions (sender_wallet_id, receiver_wallet_id, amount, currency, date, transaction_status_id, description, transaction_type_id)
VALUES
    (1, 3, 200.00, ''USD'', ''2024-03-11 12:30:00'', 2, ''Payment for services'', 2),
    (2, 4, 200.00, ''USD'', ''2024-03-11 12:30:00'', 2, ''Reimbursement for dinner'', 2),
    (3, 5, 200.00, ''USD'', ''2024-03-12 12:30:00'', 2, ''Payment for services'', 2),
    (4, 6, 50.00, ''EUR'', ''2024-03-13 13:45:00'', 2, ''Reimbursement for dinner'', 2);

-- Example data for recurring_transactions
INSERT INTO recurring_transactions (transaction_id, intervals, start_date, end_date)
VALUES
    (1, ''MONTHLY'', ''2024-03-13'', ''2024-12-01''),
    (3, ''MONTHLY'', ''2024-04-01'', ''2024-12-01'');

-- Example data for referrals
INSERT INTO referrals (user_id, referred_email, referral_status, bonus) VALUES
                                                                            (1, ''friend1@example.com'', ''PENDING'', 10.00),
                                                                            (2, ''friend2@example.com'', ''COMPLETED'', 20.00);

-- Example data for overdrafts_types
INSERT INTO overdrafts_types (overdraft_name, overdraft_limit, overdraft_interest, duration)
VALUES
    (''Standard'', 500.00, 10, ''2024-03-11'');

-- Example data for overdrafts
INSERT INTO overdrafts (wallet_id, overdraft_type_id, start_date, due_date, is_paid)
VALUES
    (1, 1, ''2024-03-11'', ''2024-03-31'', false),
    (2, 1, ''2024-03-11'', ''2024-03-31'', true);

-- Example data for savings_types
INSERT INTO savings_types (saving_name, saving_amount, saving_interest)
VALUES
    (''Regular'', 1000.00, 5),
    (''High-Yield'', 5000.00, 10);

-- Example data for savings
INSERT INTO savings (wallet_id, saving_type_id, start_date, due_date)
VALUES
    (1, 1, ''2024-03-11'', ''2025-03-11''),
    (2, 2, ''2024-03-11'', ''2025-03-11'');

-- Example data for spending_categories
INSERT INTO spending_categories (category_name) VALUES
                                                    (''Food''),
                                                    (''Utilities''),
                                                    (''Transportation'');

-- Example data for transfers
INSERT INTO transfers (wallet_id, account_number, amount, currency, direction, date, status, category_id)
VALUES
    (1, ''1234567812345678'', 50.00, ''USD'', ''OUTGOING'', ''2024-03-11 15:00:00'', ''COMPLETED'', 1),
    (2, ''9876543298765432'', 25.00, ''EUR'', ''INCOMING'', ''2024-03-11 16:30:00'', ''PENDING'', 2);25-03-11');

-- Example data for spending_categories
INSERT INTO spending_categories (category_name) VALUES
                                                    ('Food'),
                                                    ('Utilities'),
                                                    ('Transportation');

-- Example data for transfers
INSERT INTO transfers (wallet_id, account_number, amount, currency, direction, date, status, category_id)
VALUES
    (1, '1234567812345678', 50.00, 'USD', 'OUTGOING', '2024-03-11 15:00:00', 'COMPLETED', 1),
    (2, '9876543298765432', 25.00, 'EUR', 'INCOMING', '2024-03-11 16:30:00', 'PENDING', 2);