insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, '100p', 'http://stream.100p.nl/100pctnl.mp3', true);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, '538','http://playerservices.streamtheworld.com/api/livestream-redirect/RADIO538.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Qmusic', 'http://icecast-qmusic.cdp.triple-it.nl/Qmusic_nl_live_96.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'NPO 3FM', 'http://icecast.omroep.nl/3fm-bb-mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'SkyRadio','http://playerservices.streamtheworld.com/api/livestream-redirect/SKYRADIOAAC.aac', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Veronica', 'http://21253.live.streamtheworld.com/VERONICA.mp3', false);
insert into webradios (id, name, url, is_default)
values (next value for hibernate_sequence, 'Radio 10', 'http://20403.live.streamtheworld.com/RADIO10.mp3', false);

commit;
