-- Заполняем таблицу категорий фильмов
MERGE INTO genres (genre_id, genre_name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, genre_name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, genre_name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, genre_name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, genre_name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, genre_name) VALUES (6, 'Боевик');

-- Заполняем таблицу рейтинга фильма
MERGE INTO mpa_rating (mpa_rating_id, mpa_name) VALUES (1, 'G');
MERGE INTO mpa_rating (mpa_rating_id, mpa_name) VALUES (2, 'PG');
MERGE INTO mpa_rating (mpa_rating_id, mpa_name) VALUES (3, 'PG-13');
MERGE INTO mpa_rating (mpa_rating_id, mpa_name) VALUES (4, 'R');
MERGE INTO mpa_rating (mpa_rating_id, mpa_name) VALUES (5, 'NC-17');
