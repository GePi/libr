create table persistent_logins
(
    series    varchar(64) not null
        primary key,
    username  varchar(64) not null,
    token     varchar(64) not null,
    last_used timestamp   not null
);

alter table persistent_logins
    owner to libuser;

