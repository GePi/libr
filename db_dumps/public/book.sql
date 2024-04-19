create table book
(
    id           bigint        not null
        constraint pk_book
            primary key,
    version      integer       not null,
    lang         varchar(2),
    title_en     varchar(1000) not null,
    title_ru     varchar(1000) not null,
    file_name_ru varchar(255),
    file_name_en varchar(255),
    owner_id     uuid
        constraint fk_book_on_owner
            references user_
);

alter table book
    owner to libuser;

