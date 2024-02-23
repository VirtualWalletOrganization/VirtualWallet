create table users
(
    user_id           int auto_increment
        primary key,
    username          varchar(20)  not null,
    password          varchar(50)  not null,
    email             varchar(100) not null,
    phone_number      varchar(10)  not null,
    photo             varchar(255) not null,
    card_id           varchar(20)  not null,
    email_verified    tinyint(1)   not null,
    identity_verified tinyint(1)   not null,
    overdraft_enabled tinyint(1)   not null,
    overdraft_limit   decimal      not null,
    isAdmin           tinyint(1)   not null,
    constraint users_pk_2
        unique (username),
    constraint users_pk_3
        unique (email),
    constraint users_pk_4
        unique (phone_number)
);



create table wallet_table
(
    wallet_id   int auto_increment
        primary key,
    creator_id  int         not null,
    wallet_name varchar(50) not null,
    isDefault   tinyint(1)  not null
);

create table cards
(
    card_id         int auto_increment
        primary key,
    card_type       enum ('CREDIT', 'DEBIT') not null,
    user_id         int                      not null,
    wallet_id       int                      not null,
    card_number     varchar(30)              not null,
    expiration_date date                     not null,
    card_holder     varchar(30)              not null,
    check_number    varchar(3)               not null,
    constraint credit_cards_pk_2
        unique (card_number),
    constraint cards_wallet_table_wallet_id_fk
        foreign key (wallet_id) references virtual_wallets.wallet_table (wallet_id),
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
        foreign key (wallet_id) references virtual_wallets.wallet_table (wallet_id)
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

create table external_transactions
(
    transaction_id   int auto_increment
        primary key,
    sender_card_id   int                           not null,
    receiver_card_id int                           not null,
    amount           decimal                       not null,
    direction        enum ('incoming', 'outgoing') not null,
    date             date                          not null,
    transaction_code varchar(100)                  not null,
    constraint external_transactions_cards_card_id_fk
        foreign key (sender_card_id) references virtual_wallets.cards (card_id),
    constraint external_transactions_cards_card_id_fk_2
        foreign key (receiver_card_id) references virtual_wallets.cards (card_id)
);

create table internal_transactions
(
    internal_transaction_id    int auto_increment
        primary key,
    sender_card_id             int                           not null,
    receiver_savings_wallet_id int                           not null,
    amount                     decimal                       not null,
    direction                  enum ('incoming', 'outgoing') not null,
    date                       date                          not null
);

create table overdrafts
(
    overdraft_id      int auto_increment
        primary key,
    user_id           int           not null,
    card_id           int           not null,
    overdraft_enabled tinyint(1)    not null,
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
    sender_id                int                                 not null,
    receiver_id              int                                 not null,
    amount                   decimal                             not null,
    intervals                enum ('daily', 'weekly', 'monthly') not null,
    start_date               date                                not null,
    end_date                 date                                not null,
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
    referral_status  enum ('completed', 'expired', 'pending') not null,
    constraint referrals_users_user_id_fk
        foreign key (referrer_user_id) references virtual_wallets.users (user_id)
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
    constraint transactions_categories_external_transactions_transaction_id_fk
        foreign key (transaction_id) references virtual_wallets.external_transactions (transaction_id),
    constraint transactions_categories_spending_categories_category_id_fk
        foreign key (category_id) references virtual_wallets.spending_categories (category_id)
);




