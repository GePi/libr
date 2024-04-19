create table tag
(
    id       bigint       not null
        constraint pk_tag
            primary key,
    title    varchar(255) not null,
    owner_id uuid
        constraint fk_tag_on_owner
            references user_,
    version  integer      not null
);

alter table tag
    owner to libuser;

create index idx_tag_owner
    on tag (owner_id);

