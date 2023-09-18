insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, '100p', 'http://stream.100p.nl/100pctnl.mp3', true);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, '538','http://playerservices.streamtheworld.com/api/livestream-redirect/RADIO538.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Qmusic', 'https://stream.qmusic.nl/qmusic/mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'NPO 3FM', 'https://icecast.omroep.nl/3fm-bb-mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'SkyRadio','http://playerservices.streamtheworld.com/api/livestream-redirect/SKYRADIO.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Veronica', 'http://playerservices.streamtheworld.com/api/livestream-redirect/VERONICA.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Radio 10', 'http://playerservices.streamtheworld.com/api/livestream-redirect/RADIO10.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'KINK', 'https://playerservices.streamtheworld.com/api/livestream-redirect/KINK.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Arrow Classic Rock', 'https://stream.player.arrow.nl/arrow', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Joe', 'https://stream.joe.nl/joe/mp3', false);

commit;
