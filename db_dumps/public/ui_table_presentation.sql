create table ui_table_presentation
(
    id            uuid not null
        constraint "UI_TABLE_PRESENTATION_pkey"
            primary key,
    create_ts     timestamp,
    created_by    varchar(50),
    component     varchar(255),
    name          varchar(255),
    settings      varchar(4000),
    username      varchar(255),
    is_auto_save  boolean,
    update_ts     timestamp,
    updated_by    varchar(50),
    sys_tenant_id varchar(255),
    is_default    boolean
);

alter table ui_table_presentation
    owner to libuser;

