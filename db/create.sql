create table photos_verifications
(
    photo_id      int auto_increment primary key,
    photo_card_id varchar(255) null,
    selfie        varchar(255) null
);

CREATE TABLE identity_statuses
(
    identity_status_id         INT AUTO_INCREMENT PRIMARY KEY,
    identity_verification_name enum ('APPROVED', 'INCOMPLETE', 'PENDING', 'REJECTED') default 'INCOMPLETE' null
);

CREATE TABLE users_roles
(
    role_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_role_name enum ('ADMIN', 'USER') NOT NULL DEFAULT 'USER'
);

CREATE TABLE wallets_roles
(
    wallet_role_id   INT AUTO_INCREMENT PRIMARY KEY,
    wallet_role_name enum ('ADMIN', 'USER') NOT NULL DEFAULT 'USER'
);

create table users
(
    user_id            int auto_increment primary key,
    first_name         VARCHAR(32)                NOT NULL,
    last_name          VARCHAR(32)                NOT NULL,
    username           varchar(20) unique         not null,
    password           varchar(50)                not null,
    email              varchar(100) unique        not null,
    phone_number       varchar(10) unique         not null,
    profile_picture    varchar(255)               null,
    email_verified     tinyint(1) default 0       null,
    identity_status_id int                        not null,
    photo_id           int                        null,
    role_id            int                        not null,
    is_deleted         tinyint(1) default 0       null,
    status             enum ('ACTIVE', 'BLOCKED') null,
    wallet_role_id     int                        null,
    CONSTRAINT users_identity_statuses_identity_status_id_fk
        FOREIGN KEY (identity_status_id) REFERENCES identity_statuses (identity_status_id),
    constraint users_photos_verifications_photo_id_fk
        foreign key (photo_id) references photos_verifications (photo_id),
    constraint users_users_roles_role_id_fk
        foreign key (role_id) references users_roles (role_id),
    constraint users_wallets_roles_wallet_role_id_fk
        foreign key (wallet_role_id) references wallets_roles (wallet_role_id)
);

create table contacts
(
    contact_id   int auto_increment primary key,
    user_id      int                not null,
    username     varchar(50) unique not null,
    phone_number varchar(10) unique not null,
    constraint users_contacts_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

CREATE TABLE cards_types
(
    card_type_id   INT AUTO_INCREMENT PRIMARY KEY,
    card_type_name enum ('CREDIT', 'DEBIT') not null
);

create table cards
(
    card_id         int auto_increment primary key,
    card_type_id    int                            not null,
    user_id         int                            not null,
    card_number     varchar(16) unique             not null,
    expiration_date date                           not null,
    card_holder     varchar(30)                    not null,
    check_number    varchar(3)                     not null,
    currency        varchar(3)                     not null,
    status          enum ('ACTIVE', 'DEACTIVATED') not null,
    constraint cards_cards_types_card_type_id_fk
        foreign key (card_type_id) references cards_types (card_type_id),
    constraint cards_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

CREATE TABLE wallets_types
(
    wallet_type_id   INT AUTO_INCREMENT PRIMARY KEY,
    wallet_type_name enum ('JOINT', 'REGULAR') not null default 'REGULAR'
);

create table wallets
(
    wallet_id         int auto_increment primary key,
    creator_id        int                   not null,
    balance           decimal default 0.00  not null,
    currency          varchar(3)            not null,
    wallet_type_id    int                   not null,
    is_default        boolean default false not null,
    is_deleted        boolean default false not null,
    overdraft_enabled boolean default false not null,
    saving_enabled    boolean default false not null,
    constraint wallets_users_user_id_fk
        foreign key (creator_id) references users (user_id),
    constraint wallets_wallets_types_wallet_type_id_fk
        foreign key (wallet_type_id) references wallets_types (wallet_type_id)
);

create table cards_wallets
(
    card_id   int null,
    wallet_id int null,
    constraint cards_wallets_cards_card_id_fk
        foreign key (card_id) references cards (card_id),
    constraint cards_wallets_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

create table users_wallets
(
    user_id   int not null,
    wallet_id int not null,
    constraint users_wallets_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint users_wallets_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

CREATE TABLE transactions_statuses
(
    transaction_status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name           enum ('COMPLETED', 'FAILED', 'PENDING') not null
);

CREATE TABLE transactions_types
(
    transaction_type_id   INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type_name enum ('SINGLE', 'RECURRING') not null
);

create table transactions
(
    transaction_id        int auto_increment primary key,
    sender_wallet_id      int                           not null,
    receiver_wallet_id    int                           not null,
    amount                decimal                       not null,
    currency              varchar(3)                    not null,
    direction             enum ('INCOMING', 'OUTGOING') not null,
    date                  date                          not null,
    transaction_status_id int                           not null,
    description           varchar(255)                  not null,
    transaction_type_id   int                           not null,
    constraint transactions_wallets_wallet_id_fk
        foreign key (receiver_wallet_id) references wallets (wallet_id),
    constraint transactions_wallets_wallet_id_fk_2
        foreign key (sender_wallet_id) references wallets (wallet_id),
    constraint transactions_transactions_statuses_transaction_status_id_fk
        foreign key (transaction_status_id) references transactions_statuses (transaction_status_id),
    constraint transactions_transactions_types_transaction_type_id_fk
        foreign key (transaction_type_id) references transactions_types (transaction_type_id)
);

create table recurring_transactions
(
    recurring_transaction_id int auto_increment primary key,
    transaction_id           int                                 not null,
    intervals                enum ('DAILY', 'MONTHLY', 'WEEKLY') not null,
    start_date               date                                not null,
    end_date                 date                                not null,
    constraint recurring_transactions_transactions_transaction_id_fk
        foreign key (transaction_id) references transactions (transaction_id)
);

create table referrals
(
    referral_id     int auto_increment primary key,
    user_id         int                                      not null,
    referred_email  varchar(50)                              not null,
    referral_status enum ('COMPLETED', 'EXPIRED', 'PENDING') not null,
    bonus           decimal                                  not null,
    constraint referrals_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table overdrafts_types
(
    overdraft_type_id  int auto_increment primary key,
    overdraft_name     varchar(50) not null,
    overdraft_limit    decimal     not null,
    overdraft_interest int         not null,
    duration           date        not null
);

create table overdrafts
(
    overdraft_id      int auto_increment primary key,
    wallet_id         int                   not null,
    overdraft_type_id int                   not null,
    start_date        date                  not null,
    due_date          date                  not null,
    is_paid           boolean default false not null,
    constraint overdrafts_overdrafts_types_overdraft_type_id_fk
        foreign key (overdraft_type_id) references overdrafts_types (overdraft_type_id),
    constraint overdrafts_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

create table savings_types
(
    saving_type_id  int auto_increment primary key,
    saving_name     varchar(50) null,
    saving_amount   decimal     null,
    saving_interest int         null
);

create table savings
(
    saving_id      int auto_increment primary key,
    wallet_id      int  not null,
    saving_type_id int  null,
    start_date     date not null,
    due_date       date not null,
    constraint savings_savings_types_saving_type_id_fk
        foreign key (saving_type_id) references savings_types (saving_type_id),
    constraint savings_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

create table spending_categories
(
    category_id   int auto_increment primary key,
    category_name varchar(50) not null
);

create table transfers
(
    transfer_id    int auto_increment primary key,
    wallet_id      int                                     not null,
    account_number varchar(16) unique                      not null,
    amount         decimal                                 not null,
    currency       varchar(3)                              not null,
    direction      enum ('INCOMING', 'OUTGOING')           not null,
    date           date                                    not null,
    status         enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    category_id    int                                     not null,
    constraint transfers_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id),
    constraint transfers_spending_categories_category_id_fk
        foreign key (category_id) references spending_categories (category_id)
);