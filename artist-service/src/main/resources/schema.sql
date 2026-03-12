drop table if exists artist_genre;
drop table if exists artist_social_media_links;
drop table if exists artist;

CREATE TABLE artist
(
    id             INT PRIMARY KEY auto_increment,
    artist_id      VARCHAR(255) NOT NULL unique,
    first_name     VARCHAR(255),
    last_name      VARCHAR(255),
    biography_text TEXT
);

CREATE TABLE artist_genre
(
    id        INT PRIMARY KEY auto_increment,

    artist_id VARCHAR(255) NOT NULL,
    genre     VARCHAR(255),
    FOREIGN KEY (artist_id) REFERENCES artist (artist_id)
);

CREATE TABLE artist_social_media_links
(
    id        INT PRIMARY KEY auto_increment,

    artist_id VARCHAR(255) NOT NULL,
    platform  VARCHAR(255) NOT NULL,
    uri       VARCHAR(355) NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES artist (artist_id)
);

