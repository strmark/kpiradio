create table alarms
(
    id                bigint  not null,
    name              varchar(255),
    monday            boolean not null,
    tuesday           boolean not null,
    wednesday         boolean not null,
    thursday          boolean not null,
    friday            boolean not null,
    saturday          boolean not null,
    sunday            boolean not null,
    hours             integer not null,
    minutes           integer not null,
    auto_stop_minutes integer not null,
    is_active         boolean not null,
    web_radio         bigint  not null,
    constraint alarms_pk primary key (id)
);

create unique index alarms_pk_ind on alarms (id);
