insert into MPA_RATINGS (name)
select 'G' from dual
where not exists (select 1 from MPA_RATINGS where name = 'G');

insert into MPA_RATINGS (name)
select 'PG' from dual
where not exists (select 1 from MPA_RATINGS where name = 'PG');

insert into MPA_RATINGS (name)
select 'PG-13' from dual
where not exists (select 1 from MPA_RATINGS where name = 'PG-13');

insert into MPA_RATINGS (name)
select 'R' from dual
where not exists (select 1 from MPA_RATINGS where name = 'R');

insert into MPA_RATINGS (name)
select 'NC-17' from dual
where not exists (select 1 from MPA_RATINGS where name = 'NC-17');

insert into GENRES (name)
select 'Аниме' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'аниме');

insert into GENRES (name)
select 'Биографический' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'биографический');

insert into GENRES (name)
select 'Боевик' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'боевик');

insert into GENRES (name)
select 'Вестерн' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'вестерн');

insert into GENRES (name)
select 'Военный' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'военный');

insert into GENRES (name)
select 'Детектив' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'детектив');

insert into GENRES (name)
select 'Детский' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'детский');

insert into GENRES (name)
select 'Документальный' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'документальный');

insert into GENRES (name)
select 'Драма' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'драма');

insert into GENRES (name)
select 'Исторический' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'исторический');

insert into GENRES (name)
select 'Комедия' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'комедия');

insert into GENRES (name)
select 'Короткометражный' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'короткометражный');

insert into GENRES (name)
select 'Криминал' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'криминал');

insert into GENRES (name)
select 'Мелодрама' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'мелодрама');

insert into GENRES (name)
select 'Мистика' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'мистика');

insert into GENRES (name)
select 'Мультфильм' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'мультфильм');

insert into GENRES (name)
select 'Мюзикл' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'мюзикл');

insert into GENRES (name)
select 'Научный' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'научный');

insert into GENRES (name)
select 'Нуар' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'нуар');

insert into GENRES (name)
select 'Приключения' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'приключения');

insert into GENRES (name)
select 'Семейный' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'семейный');

insert into GENRES (name)
select 'Спорт' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'спорт');

insert into GENRES (name)
select 'Триллер' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'триллер');

insert into GENRES (name)
select 'Ужасы' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'ужасы');

insert into GENRES (name)
select 'Фантастика' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'фантастика');

insert into GENRES (name)
select 'Фэнтези' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'фэнтези');

insert into GENRES (name)
select 'Эротика' from dual
where not exists (select 1 from GENRES where LOWER(name) = 'эротика');