create table ui_setting
(
    id         uuid not null
        constraint "UI_SETTING_pkey"
            primary key,
    create_ts  timestamp,
    created_by varchar(50),
    username   varchar(255),
    name       varchar(255),
    value_     text
);

alter table ui_setting
    owner to libuser;

