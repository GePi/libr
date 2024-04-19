create table ui_filter_configuration
(
    id               uuid         not null
        constraint "UI_FILTER_CONFIGURATION_pkey"
            primary key,
    component_id     varchar(255) not null,
    configuration_id varchar(255) not null,
    username         varchar(255),
    root_condition   text,
    sys_tenant_id    varchar(255),
    name             varchar(255),
    default_for_all  boolean
);

alter table ui_filter_configuration
    owner to libuser;

