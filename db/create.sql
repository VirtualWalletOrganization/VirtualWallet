create table photos_verifications
(
    photo_id      int auto_increment primary key,
    photo_card_id varchar(255) null,
    selfie        varchar(255) null
);

create table users
(
    user_id           int auto_increment primary key,
    username          varchar(20) unique  not null,
    password          varchar(50)         not null,
    email             varchar(100) unique not null,
    phone_number      varchar(10) unique  not null,
    profile_picture   varchar(255) null,
    email_verified    tinyint(1) default 0 null,
    identity_verified enum ('APPROVED', 'INCOMPLETE', 'PENDING', 'REJECTED') default 'INCOMPLETE' null,
    photo_id          int null,
    role              enum ('ADMIN', 'USER') null,
    is_deleted        tinyint(1) default 0 null,
    status            enum ('ACTIVE', 'BLOCKED') null,
    wallet_admin      enum ('ADMIN', 'REGULAR') null,
    constraint users_photos_verifications_photo_id_fk
        foreign key (photo_id) references photos_verifications (photo_id)
);

create table contacts
(
    contact_id   int auto_increment primary key,
    user_id      int                not null,
    username     varchar(50) unique not null,
    phone_number varchar(10) unique not null,
    constraint users_contacts_users_user_id_fk
        foreign key (user_id) references users (user_id),
);

create table cards
(
    card_id         int auto_increment primary key,
    card_type       enum ('CREDIT', 'DEBIT') not null,
    user_id         int                not null,
    card_number     varchar(16) unique not null,
    expiration_date date               not null,
    card_holder     varchar(30)        not null,
    check_number    varchar(3)         not null,
    currency        varchar(3)         not null,
    status          enum ('ACTIVE', 'DEACTIVATED') not null,
    constraint cards_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table wallets
(
    wallet_id         int auto_increment primary key,
    creator_id        int                   not null,
    balance           decimal               not null,
    currency          varchar(3)            not null,
    wallet_type       enum ('JOINT', 'REGULAR') not null,
    is_default        boolean default false not null,
    is_deleted        boolean default false not null,
    overdraft_enabled boolean default false not null,
    saving_enabled    boolean default false not null,
    constraint wallets_users_user_id_fk
        foreign key (creator_id) references users (user_id)
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

create table referrals
(
    referral_id     int auto_increment primary key,
    user_id         int         not null,
    referred_email  varchar(50) not null,
    referral_status enum ('COMPLETED', 'EXPIRED', 'PENDING') not null,
    bonus           decimal     not null,
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
    saving_amount   decimal null,
    saving_interest int null
);

create table savings
(
    saving_id      int auto_increment primary key,
    wallet_id      int  not null,
    saving_type_id int null,
    start_date     date not null,
    due_date       date not null,
    constraint savings_savings_types_saving_type_id_fk
        foreign key (saving_type_id) references savings_types (saving_type_id),
    constraint savings_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);

create table transactions
(
    transaction_id     int auto_increment primary key,
    sender_wallet_id   int          not null,
    receiver_wallet_id int          not null,
    amount             decimal      not null,
    currency           varchar(3)   not null,
    direction          enum ('INCOMING', 'OUTGOING') not null,
    date               date         not null,
    status             enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    description        varchar(255) not null,
    constraint transactions_wallets_wallet_id_fk
        foreign key (receiver_wallet_id) references wallets (wallet_id),
    constraint transactions_wallets_wallet_id_fk_2
        foreign key (sender_wallet_id) references wallets (wallet_id)
);

create table recurring_transactions
(
    recurring_transaction_id int auto_increment primary key,
    sender_wallet_id         int          not null,
    receiver_wallet_id       int          not null,
    amount                   decimal      not null,
    currency                 varchar(3)   not null,
    intervals                enum ('DAILY', 'MONTHLY', 'WEEKLY') not null,
    date                     date         not null,
    start_date               date         not null,
    end_date                 date         not null,
    status                   enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    description              varchar(255) not null,
    constraint recurring_transactions_wallets_wallet_id_fk
        foreign key (sender_wallet_id) references wallets (wallet_id),
    constraint recurring_transactions_wallets_wallet_id_fk_2
        foreign key (receiver_wallet_id) references wallets (wallet_id)
);

create table spending_categories
(
    category_id   int auto_increment primary key,
    category_name varchar(50) not null
);

create table transfers
(
    transfer_id int auto_increment primary key,
    wallet_id   int        not null,
    card_id     int        not null,
    amount      decimal    not null,
    currency    varchar(3) not null,
    direction   enum ('INCOMING', 'OUTGOING') not null,
    date        date       not null,
    status      enum ('COMPLETED', 'FAILED', 'PENDING') not null,
    category_id int        not null,
    constraint transfers_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id),
    constraint transfers_cards_card_id_fk
        foreign key (card_id) references cards (card_id),
    constraint transfers_spending_categories_category_id_fk
        foreign key (category_id) references spending_categories (category_id)
);