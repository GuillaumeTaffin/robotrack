CREATE TABLE IF NOT EXISTS airplanes
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR NOT NULL UNIQUE,
    latitude  INT2    NOT NULL DEFAULT 0,
    longitude INT2    NOT NULL DEFAULT 0
);