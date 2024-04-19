create table book_tags
(
    book_id bigint  not null
        constraint fk_book_tags_on_book
            references book
            on delete cascade,
    tag_id  bigint  not null
        constraint fk_book_tags_on_tag
            references tag
            on delete cascade,
    version integer not null,
    constraint pk_book_tags
        primary key (book_id, tag_id)
);

alter table book_tags
    owner to libuser;

create index idx_book_tags_tag
    on book_tags (tag_id);

create index idx_book_tags_book
    on book_tags (book_id);

