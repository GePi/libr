create table tag_scheme
(
    id       bigint       not null
        constraint pk_tag_scheme
            primary key,
    version  integer      not null,
    owner_id uuid         not null
        constraint fk_tag_scheme_on_owner
            references user_
            on delete cascade,
    title    varchar(255) not null,
    def      boolean      not null
);

alter table tag_scheme
    owner to libuser;

create index idx_tag_scheme_owner
    on tag_scheme (owner_id);

