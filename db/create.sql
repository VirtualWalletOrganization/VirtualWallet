create table overdrafts_types
(
    ovrdraft_type_id  int auto_increment
        primary key,
    overdraft_name    varchar(50) not null,
    overdraft_limit   int         not null,
    overdaft_interest int         not null
);

create table photos
(
    photo_id        int auto_increment
        primary key,
    photo_card_id   varchar(255) null,
    selfie          varchar(255) null,
    profile_picture varchar(255) null
);

create table savings_types
(
    saving_type_id  int auto_increment
        primary key,
    saving_name     varchar(50) null,
    saving_amount   decimal     null,
    saving_interest int         null
);

create table savings
(
    saving_id      int auto_increment
        primary key,
    wallet_id      int     not null,
    saving_type_id int     null,
    balance        decimal not null,
    start_date     date    not null,
    due_date       date    not null,
    constraint savings_savings_types_saving_type_id_fk
        foreign key (saving_type_id) references savings_types (saving_type_id)
);

create index saving_wallet_wallets_wallet_id_fk
    on savings (wallet_id);

create table spending_categories
(
    category_id   int auto_increment
        primary key,
    category_name varchar(50) not null
);

create table users
(
    user_id           int auto_increment
        primary key,
    username          varchar(20)                                                                 not null,
    password          varchar(50)                                                                 not null,
    email             varchar(100)                                                                not null,
    phone_number      varchar(10)                                                                 not null,
    email_verified    tinyint(1)                                             default 0            null,
    identity_verified enum ('INCOMPLETE', 'PENDING', 'APPROVED', 'REJECTED') default 'INCOMPLETE' null,
    photo_id          int                                                                         null,
    role              enum ('ADMIN', 'USER')                                                      null,
    is_deleted        tinyint(1)                                             default 0            null,
    status            enum ('BLOCKED', 'ACTIVE')                                                  null,
    constraint users_pk_2
        unique (username),
    constraint users_pk_3
        unique (email),
    constraint users_pk_4
        unique (phone_number),
    constraint users_photos_photo_id_fk
        foreign key (photo_id) references photos (photo_id)
);

create table cards
(
    card_id         int auto_increment
        primary key,
    card_type       enum ('CREDIT', 'DEBIT') not null,
    user_id         int                      not null,
    card_number     varchar(16)              not null,
    expiration_date date                     not null,
    card_holder     varchar(30)              not null,
    check_number    varchar(3)               not null,
    balance         decimal                  not null,
    constraint credit_cards_pk_2
        unique (card_number),
    constraint credit_cards_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table contacts
(
    user_id         int not null,
    contact_user_id int not null,
    constraint contacts_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint contacts_users_user_id_fk_2
        foreign key (contact_user_id) references users (user_id)
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
        foreign key (referrer_user_id) references users (user_id)
);

create table wallets
(
    wallet_id   int auto_increment
        primary key,
    creator_id  int                                              not null,
    balance     decimal                                          not null,
    wallet_type enum ('JOINT', 'OVERDRAFT', 'REGULAR', 'SAVING') not null,
    currency    varchar(3)                                       not null,
    isDefault   tinyint(1) default 0                             null,
    role        enum ('ADMIN', 'USER')                           null,
    is_deleted  tinyint(1) default 0                             null
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

create table overdrafts
(
    overdraft_id      int auto_increment
        primary key,
    wallet_id         int                  not null,
    overdraft_enabled tinyint(1) default 0 null,
    balance           decimal              not null,
    overdraft_type_id int                  null,
    start_date        date                 not null,
    due_date          date                 not null,
    constraint overdrafts_overdrafts_types_ovrdraft_type_id_fk
        foreign key (overdraft_type_id) references overdrafts_types (ovrdraft_type_id),
    constraint overdrafts_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

create table recurring_transactions
(
    recurring_transaction_id int auto_increment
        primary key,
    sender_wallet_id         int                                     not null,
    receiver_wallet_id       int                                     not null,
    amount                   decimal                                 not null,
    intervals                enum ('DAILY', 'WEEKLY', 'MONTHLY')     not null,
    start_date               date                                    not null,
    end_date                 date                                    not null,
    status                   enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    description              varchar(255)                            not null,
    constraint recurring_transactions_wallets_wallet_id_fk
        foreign key (sender_wallet_id) references wallets (wallet_id),
    constraint recurring_transactions_wallets_wallet_id_fk_2
        foreign key (receiver_wallet_id) references wallets (wallet_id)
);

create table transactions
(
    transaction_id     int auto_increment
        primary key,
    sender_wallet_id   int                                     not null,
    receiver_wallet_id int                                     not null,
    amount             decimal                                 not null,
    currency           varchar(3)                              not null,
    direction          enum ('INCOMING', 'OUTGOING')           not null,
    date               date                                    not null,
    status             enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    description        varchar(255)                            not null,
    constraint transactions_wallets_wallet_id_fk
        foreign key (receiver_wallet_id) references wallets (wallet_id),
    constraint transactions_wallets_wallet_id_fk_2
        foreign key (sender_wallet_id) references wallets (wallet_id)
);

create table transfers
(
    transaction_id int auto_increment
        primary key,
    wallet_id      int                                     not null,
    card_id        int                                     not null,
    amount         decimal                                 not null,
    currency       varchar(3)                              not null,
    direction      enum ('INCOMING', 'OUTGOING')           not null,
    date           date                                    not null,
    status         enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    description    varchar(255)                            not null,
    constraint transfers_cards_card_id_fk
        foreign key (card_id) references cards (card_id),
    constraint transfers_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

create table transactions_categories
(
    transfer_out_id int not null,
    category_id     int not null,
    constraint transactions_categories_external_transfers_out_transaction_id_fk
        foreign key (transfer_out_id) references transfers (transaction_id),
    constraint transactions_categories_spending_categories_category_id_fk
        foreign key (category_id) references spending_categories (category_id)
);

create table users_wallets
(
    user_id   int not null,
    wallet_id int not null,
    constraint user_wallet_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint user_wallet_wallet_table_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

