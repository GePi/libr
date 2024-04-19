create table sec_resource_role
(
    id            uuid              not null
        constraint "SEC_RESOURCE_ROLE_pkey"
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
    description   text,
    scopes        varchar(1000)
);

alter table sec_resource_role
    owner to libuser;

create index idx_resource_role_un_c
    on sec_resource_role (code)
    where (delete_ts IS NULL);

