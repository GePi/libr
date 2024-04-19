create table sec_role_assignment
(
    id         uuid              not null
        constraint "SEC_ROLE_ASSIGNMENT_pkey"
            primary key,
    version    integer default 1 not null,
    create_ts  timestamp,
    created_by varchar(50),
    update_ts  timestamp,
    updated_by varchar(50),
    delete_ts  timestamp,
    deleted_by varchar(50),
    username   varchar(255)      not null,
    role_code  varchar(255)      not null,
    role_type  varchar(255)      not null
);

alter table sec_role_assignment
    owner to libuser;

