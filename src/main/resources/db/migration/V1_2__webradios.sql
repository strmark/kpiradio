create table webradios
(
    id         bigint  not null,
    name       varchar(255),
    url        varchar(255),
    is_default boolean not null,
    constraint web_radios_pk primary key (id)
);

create unique index web_radios_pk_ind on webradios (id);
