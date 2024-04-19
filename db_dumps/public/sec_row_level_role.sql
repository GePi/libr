create table sec_row_level_role
(
    id            uuid              not null
        constraint "SEC_ROW_LEVEL_ROLE_pkey"
            primary key,
    version       integer default 1 not null,
    create_ts     timestamp,
    created_by    varchar(50),
    update_ts     timestamp,
    updated_by    varchar(50),
    delete_ts     timestamp,
    deleted_by    varchar(50),
    name          varchar(255)      not null,
    code          varchar(255)      not null,
    child_roles   text,
    sys_tenant_id varchar(255),
    description   text
);

alter table sec_row_level_role
    owner to libuser;

create index idx_row_level_role_un_c
    on sec_row_level_role (code)
    where (delete_ts IS NULL);

