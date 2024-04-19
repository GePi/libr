create table user_
(
    id           uuid         not null
        constraint "USER__pkey"
            primary key,
    version      integer      not null,
    username     varchar(255) not null,
    first_name   varchar(255),
    last_name    varchar(255),
    password     varchar(255),
    email        varchar(255),
    active       boolean,
    time_zone_id varchar(255)
);

alter table user_
    owner to libuser;

create unique index idx_user__on_username
    on user_ (username);

