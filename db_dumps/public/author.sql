create table author
(
    id      bigint  not null
        constraint pk_author
            primary key,
    version integer not null,
    name_en varchar(255),
    name_ru varchar(255)
);

alter table author
    owner to libuser;

