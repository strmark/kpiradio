create table webradios
(
    id         bigint  not null,
    name       varchar(255),
    url        varchar(255),
    is_default boolean not null,
    constraint webradios_pk primary key (id)
);

create unique index webradios_pk_ind on webradios (id);
