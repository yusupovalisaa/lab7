CREATE TABLE Users
(
    id       SERIAL       PRIMARY KEY,
    login    VARCHAR(64) NOT NULL UNIQUE,
    password CHAR(56) NOT NULL -- Хэш SHA-224 имеет длину 56 символов
);

CREATE TABLE Coordinates
(
    id SERIAL PRIMARY KEY,
    x  BIGINT  NOT NULL,
    y  INTEGER NOT NULL CHECK (y <= 72)
);

CREATE TABLE Person
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    birthday    DATE,
    color       VARCHAR(6),
    nationality VARCHAR(10),
    CONSTRAINT color_check CHECK (color IN ('GREEN', 'ORANGE', 'WHITE')),
    CONSTRAINT nationality_check CHECK (nationality IN ('USA', 'FRANCE', 'ITALY'))
);

CREATE TABLE Movies
(
    id             SERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    coordinates_id INTEGER      NOT NULL,
    creation_date  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    oscars_count   BIGINT       NOT NULL CHECK (oscars_count > 0),
    genre          VARCHAR(10)  NOT NULL,
    mpaa_rating    VARCHAR(5),
    director_id    INTEGER      NOT NULL,
    user_id        INTEGER      NOT NULL,
    FOREIGN KEY (coordinates_id) REFERENCES Coordinates (id),
    FOREIGN KEY (director_id) REFERENCES Person (id),
    FOREIGN KEY (user_id) REFERENCES Users (id),
    CONSTRAINT genre_check CHECK (genre IN ('ACTION', 'WESTERN', 'COMEDY', 'TRAGEDY')),
    CONSTRAINT mpaa_rating_check CHECK (mpaa_rating IS NULL OR mpaa_rating IN ('G', 'PG', 'PG_13', 'NC_17'))
);
