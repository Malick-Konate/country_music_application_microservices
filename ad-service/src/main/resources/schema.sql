-- Drop tables if they exist (respect dependency order)
DROP TABLE IF EXISTS targeting_rules;
DROP TABLE IF EXISTS ad_creatives;
DROP TABLE IF EXISTS ad_campaigns;

-- 1. Create Ad Campaigns Table
CREATE TABLE ad_campaigns
(
    id              SERIAL PRIMARY KEY,

    ad_id           VARCHAR(255)   NOT NULL UNIQUE,
    user_id         VARCHAR(100)   NOT NULL,

    target_type     VARCHAR(20)    NOT NULL CHECK (target_type IN ('ARTIST', 'PODCAST')),
    target_id       VARCHAR(255)   NOT NULL,

    name            VARCHAR(255)   NOT NULL,

    budget          DECIMAL(19, 2) NOT NULL,
    remaining_spend DECIMAL(19, 2) NOT NULL,

    status          VARCHAR(20)    NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'PAUSED', 'EXHAUSTED'))
);

-- 2. Create Ad Creatives Table
CREATE TABLE ad_creatives
(
    id            SERIAL PRIMARY KEY,
    ad_id         VARCHAR(255) NOT NULL REFERENCES ad_campaigns (ad_id),

    media_url     VARCHAR(500),

    creative_type VARCHAR(30) CHECK (creative_type IN ('AUDIO_CLIP', 'BANNER_IMAGE', 'VIDEO_SPOT'))
);

-- 3. Create Targeting Rules Table
CREATE TABLE targeting_rules
(
    id            SERIAL PRIMARY KEY,
    ad_id         VARCHAR(255) NOT NULL REFERENCES ad_campaigns (ad_id),

    target_genre  VARCHAR(30) CHECK (target_genre IN (
                                                      'BLUEGRASS',
                                                      'HONKY_TONK',
                                                      'OUTLAW',
                                                      'COUNTRY_POP',
                                                      'ALTERNATIVE_COUNTRY',
                                                      'CANADIAN_COUNTRY',
                                                      'COUNTRY_POLITAN'
        )),

    target_region VARCHAR(30) CHECK (target_region IN (
                                                       'GLOBAL',
                                                       'NORTH_AMERICA',
                                                       'EUROPE',
                                                       'ASIA',
                                                       'LATAM'
        )),

    min_age       INT
);