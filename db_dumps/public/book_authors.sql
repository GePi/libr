create table book_authors
(
    book_id   bigint  not null
        constraint fk_book_authors_on_book
            references book,
    author_id bigint  not null
        constraint fk_book_authors_on_author
            references author,
    defaut    boolean,
    version   integer not null,
    constraint pk_book_authors
        primary key (book_id, author_id)
);

alter table book_authors
    owner to libuser;

create index idx_book_authors_author
    on book_authors (author_id);

create index idx_book_authors_book
    on book_authors (book_id);

