create table webradios
(
    id         bigint  not null,
    name       varchar2(255),
    url        varchar2(255),
    constraint web_radios_pk primary key (id)
);

create unique index web_radios_pk_ind on webradios (id);
