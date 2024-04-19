create table tag_hierarchy
(
    id        bigint  not null
        constraint pk_tag_hierarchy
            primary key,
    version   integer not null,
    scheme_id bigint  not null
        constraint fk_tag_hierarchy_on_scheme
            references tag_scheme
            on delete cascade,
    parent_id bigint
        constraint fk_tag_hierarchy_on_parent
            references tag,
    tag_id    bigint
        constraint fk_tag_hierarchy_on_tag
            references tag
);

alter table tag_hierarchy
    owner to libuser;

create unique index idx_tag_hierarchy_unq
    on tag_hierarchy (scheme_id, tag_id);

create unique index idx_tag_hierarchy_unq_1
    on tag_hierarchy (scheme_id, tag_id, parent_id);

