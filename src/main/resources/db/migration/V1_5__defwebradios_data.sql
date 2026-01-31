insert into defaultwebradio (id, web_radio_id)
select 1, id from webradios where name = '538';

commit;
