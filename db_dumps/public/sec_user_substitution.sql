create table sec_user_substitution
(
    id                   uuid              not null
        constraint "SEC_USER_SUBSTITUTION_pkey"
            primary key,
    version              integer default 1 not null,
    create_ts            timestamp,
    created_by           varchar(50),
    update_ts            timestamp,
    updated_by           varchar(50),
    delete_ts            timestamp,
    deleted_by           varchar(50),
    username             varchar(255)      not null,
    substituted_username varchar(255)      not null,
    start_date           timestamp,
    end_date             timestamp
);

alter table sec_user_substitution
    owner to libuser;

