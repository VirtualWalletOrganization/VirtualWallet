create table users
(
    user_id           int auto_increment
        primary key,
    username          varchar(20)  not null,
    password          varchar(50)  not null,
    email             varchar(100) not null,
    phone_number      varchar(10)  not null,
    profile_picture   VARCHAR(255) default '/images/users/default.png',
    selfie            varchar(255) not null,
    photo_cardId      varchar(255) not null ,
    email_verified    BOOLEAN DEFAULT FALSE,
    identity_verified ENUM ('INCOMPLETE', 'PENDING', 'APPROVED', 'REJECTED') DEFAULT 'INCOMPLETE',
    overdraft_enabled BOOLEAN DEFAULT FALSE,
    overdraft_limit   decimal (4,2)     not null,
    role              ENUM ('ADMIN', 'USER'),
    is_deleted        BOOLEAN DEFAULT FALSE,
    status            ENUM ('BLOCKED', 'ACTIVE'),
    constraint users_pk_2
        unique (username),
    constraint users_pk_3
        unique (email),
    constraint users_pk_4
        unique (phone_number)

);



create table wallets
(
    wallet_id   int auto_increment
        primary key,
    creator_id  int         not null,
    balance     decimal     not null,
    wallet_type enum('REGULAR', 'JOINT') not null,
    currency    varchar(3)  not null,
    isDefault   BOOLEAN DEFAULT FALSE,
    role              ENUM ('ADMIN', 'USER'),
    is_deleted        BOOLEAN DEFAULT FALSE

);

create table cards
(
    card_id         int auto_increment
        primary key,
    card_type       enum ('CREDIT', 'DEBIT') not null,
    user_id         int                      not null,
    wallet_id       int                      not null,
    card_number     varchar(16)              not null,
    expiration_date date                     not null,
    card_holder     varchar(30)              not null,
    check_number    varchar(3)               not null,
    balance         decimal                  not null,
    constraint credit_cards_pk_2
        unique (card_number),
    constraint cards_wallet_table_wallet_id_fk
        foreign key (wallet_id) references virtual_wallets.wallets (wallet_id),
    constraint credit_cards_users_user_id_fk
        foreign key (user_id) references virtual_wallets.users (user_id)
);


create table users_wallets
(
    user_id   int not null,
    wallet_id int not null,
    constraint user_wallet_users_user_id_fk
        foreign key (user_id) references virtual_wallets.users (user_id),
    constraint user_wallet_wallet_table_wallet_id_fk
        foreign key (wallet_id) references virtual_wallets.wallets (wallet_id)
);


create table contacts
(
    contact_id      int auto_increment
        primary key,
    user_id         int not null,
    contact_user_id int not null,
    constraint contacts_users_user_id_fk
        foreign key (user_id) references virtual_wallets.users (user_id),
    constraint contacts_users_user_id_fk_2
        foreign key (contact_user_id) references virtual_wallets.users (user_id)
);

create table internal_transactions
(
    transaction_id     int auto_increment
        primary key,
    sender_wallet_id   int                                     not null,
    receiver_wallet_id int                                     not null,
    amount             decimal                                 not null,
    currency           varchar(3)                              not null,
    direction          enum ('INCOMING', 'OUTGOING')           not null,
    date               date                                    not null,
    status             ENUM ( 'COMPLETED', 'FAILED','PENDING') NOT NULL,
    description        varchar(255)                            not null
);


create table external_transfers_out
(
    transaction_id   int auto_increment
        primary key,
    sender_wallet_id int                                     not null,
    receiver_card_id int                                     not null,
    amount           decimal                                 not null,
    currency         varchar(3)                              not null,
    direction        enum ('INCOMING', 'OUTGOING')           not null,
    date             date                                    not null,
    status           ENUM ( 'COMPLETED', 'FAILED','PENDING') NOT NULL,
    description      varchar(255)                            not null,
    constraint external_transfers_out_wallets_wallet_id_fk
        foreign key (sender_wallet_id) references wallets (wallet_id),
    constraint external_transfers_out_cards_card_id_fk
        foreign key (receiver_card_id) references cards (card_id)
);
create table external_transfers_in
(
    transaction_id   int auto_increment
        primary key,
    receiver_wallet_id int                                     not null,
    sender_card_id int                                     not null,
    amount           decimal                                 not null,
    currency         varchar(3)                              not null,
    direction        enum ('INCOMING', 'OUTGOING')           not null,
    date             date                                    not null,
    status           ENUM ( 'COMPLETED', 'FAILED','PENDING') NOT NULL,
    description      varchar(255)                            not null,
    constraint external_transfers_in_wallets_wallet_id_fk
        foreign key (receiver_wallet_id) references wallets (wallet_id),
    constraint external_transfers_in_cards_card_id_fk
        foreign key (sender_card_id) references cards (card_id)
);

create table saving_wallet
(
    saving_wallet_id int auto_increment
        primary key,
    user_id          int           not null,
    balance          decimal       not null,
    interest_rate    decimal(4, 2) not null,
    duration         int           not null,
    CONSTRAINT check_duration CHECK (duration >= 6),
    CONSTRAINT check_interest_rate
        CHECK (interest_rate >= 0.03)
);

create table saving_transactions
(
    internal_transaction_id    int auto_increment
        primary key,
    sender_wallet_id           int                                     not null,
    receiver_savings_wallet_id int                                     not null,
    amount                     decimal                                 not null,
    direction                  enum ('INCOMING', 'OUTGOING')           not null,
    date                       date                                    not null,
    status                     ENUM ( 'COMPLETED', 'FAILED','PENDING') NOT NULL,
    description                varchar(255)                            not null,
    constraint internal_transactions_saving_wallet_saving_wallet_id_fk
        foreign key (receiver_savings_wallet_id) references saving_wallet (saving_wallet_id)

);


create table overdrafts
(
    overdraft_id      int auto_increment
        primary key,
    user_id           int           not null,
    wallet_id           int           not null,
    overdraft_enabled BOOLEAN DEFAULT FALSE,
    overdraft_limit   decimal(4, 2) not null,
    interest_rate     decimal(4, 2) not null,
    last_charged_date int           not null,
    constraint check_interest_rate
        CHECK (interest_rate <= 0.10),
    CONSTRAINT check_max_overdraft_limit
        CHECK (overdraft_limit <= 1000)
);

create table recurring_transactions
(
    recurring_transaction_id int auto_increment
        primary key,
    sender_id                int                                     not null,
    receiver_id              int                                     not null,
    amount                   decimal                                 not null,
    intervals                enum ('DAILY', 'WEEKLY', 'MONTHLY')     not null,
    start_date               date                                    not null,
    end_date                 date                                    not null,
    status                   ENUM ( 'COMPLETED', 'FAILED','PENDING') NOT NULL,
    description              varchar(255)                            not null,
    constraint recurring_transactions_users_user_id_fk
        foreign key (sender_id) references virtual_wallets.users (user_id),
    constraint recurring_transactions_users_user_id_fk_2
        foreign key (receiver_id) references virtual_wallets.users (user_id)
);

create table referrals
(
    referral_id      int auto_increment
        primary key,
    referrer_user_id int                                      not null,
    referred_email   varchar(100)                             not null,
    referral_code    varchar(50)                              not null,
    referral_status  enum ('COMPLETED', 'EXPIRED', 'PENDING') not null,
    constraint referrals_users_user_id_fk
        foreign key (referrer_user_id) references virtual_wallets.users (user_id)
);


create table spending_categories
(
    category_id   int auto_increment
        primary key,
    category_name varchar(50) not null
);

create table transactions_categories
(
    transaction_id int not null,
    category_id    int not null,
    constraint transactions_categories_internal_transactions_transaction_id_fk
        foreign key (transaction_id) references virtual_wallets.internal_transactions (transaction_id),
    constraint transactions_categories_spending_categories_category_id_fk
        foreign key (category_id) references virtual_wallets.spending_categories (category_id)
);




