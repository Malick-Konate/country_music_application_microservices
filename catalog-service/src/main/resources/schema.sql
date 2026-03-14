DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS concert;


CREATE TABLE album
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    album_id     VARCHAR(255)                NOT NULL UNIQUE,
    artist_id    VARCHAR(255)                NOT NULL,
    title        VARCHAR(255)                NOT NULL,
    release_date DATE                        NOT NULL,
    record_label VARCHAR(255),
    album_type   Enum ('LP', 'EP', 'SINGLE') NOT NULL
);
DROP TABLE IF EXISTS song;

CREATE TABLE song
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    album_id VARCHAR(255) references album (album_id) ON DELETE CASCADE,
    title    VARCHAR(255) NOT NULL,
    duration TIME,
    lyrics   TEXT
);
