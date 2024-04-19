create table sec_row_level_policy
(
    id           uuid              not null
        constraint "SEC_ROW_LEVEL_POLICY_pkey"
            primary key,
    version      integer default 1 not null,
    create_ts    timestamp,
    created_by   varchar(50),
    update_ts    timestamp,
    updated_by   varchar(50),
    delete_ts    timestamp,
    deleted_by   varchar(50),
    type_        varchar(255)      not null,
    action_      varchar(255)      not null,
    entity_name  varchar(255)      not null,
    where_clause text,
    join_clause  text,
    script_      text,
    role_id      uuid              not null
        constraint fk_row_level_policy_role
            references sec_row_level_role
);

alter table sec_row_level_policy
    owner to libuser;

