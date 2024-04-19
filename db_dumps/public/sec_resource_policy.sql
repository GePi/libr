create table sec_resource_policy
(
    id           uuid              not null
        constraint "SEC_RESOURCE_POLICY_pkey"
            primary key,
    version      integer default 1 not null,
    create_ts    timestamp,
    created_by   varchar(50),
    update_ts    timestamp,
    updated_by   varchar(50),
    delete_ts    timestamp,
    deleted_by   varchar(50),
    type_        varchar(255)      not null,
    policy_group varchar(255),
    resource_    varchar(1000)     not null,
    action_      varchar(255)      not null,
    effect       varchar(255)      not null,
    role_id      uuid              not null
        constraint fk_res_policy_role
            references sec_resource_role
);

alter table sec_resource_policy
    owner to libuser;

