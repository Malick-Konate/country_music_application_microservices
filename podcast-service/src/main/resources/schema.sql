drop TABLE IF EXISTS episode;
drop TABLE IF EXISTS podcast;

CREATE TABLE podcast
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    podcast_id    VARCHAR(100)                                 NOT NULL UNIQUE,
    title         VARCHAR(200)                                 NOT NULL,
    hostname      VARCHAR(150)                                 NOT NULL,
    description   TEXT                                         NOT NULL,
    pricing_model Enum ('FREE', 'SUBSCRIPTION', 'PER_EPISODE') NOT NULL
);
CREATE TABLE episode
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    episode_id   VARCHAR(100) NOT NULL UNIQUE,
    podcast_id   VARCHAR(100) NOT NULL,
    title        VARCHAR(200) NOT NULL,
    duration     TIME         NOT NULL,
    publish_date DATE         NOT NULL,
    status       VARCHAR(30)  NOT NULL
);

