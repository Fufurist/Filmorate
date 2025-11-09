/*
INSERT INTO rating(id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17')
ON CONFLICT DO NOTHING;


INSERT INTO genre(id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик')
ON CONFLICT DO NOTHING;
--изучали postgre, чтоб в итоговом задании был другой синтаксис))
*/


insert into rating select * from (
select 1, 'G' union
select 2, 'PG' union
select 3, 'PG-13' union
select 4, 'R' union
select 5, 'NC-17'
) x where not exists(select * from rating);


insert into genre select * from (
select 1, 'Комедия' union
select 2, 'Драма' union
select 3, 'Мультфильм' union
select 4, 'Триллер' union
select 5, 'Документальный' union
select 6, 'Боевик'
) x where not exists(select * from genre);
