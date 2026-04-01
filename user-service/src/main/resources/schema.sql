DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id       serial,
    user_id  VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(150) NOT NULL UNIQUE,
    fullname VARCHAR(150) NOT NULL,
    country  VARCHAR(100) NOT NULL,
    age      INT          NOT NULL,
    PRIMARY KEY (id)

);
