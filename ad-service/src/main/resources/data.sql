INSERT INTO ad_campaigns
(ad_id, user_id, target_type, target_id, name, budget, remaining_spend, status)
VALUES ('ad_001', 'user-001', 'ARTIST', 'ART-001', 'Johnny Cash Revival', 500.00, 320.00, 'ACTIVE'),
       ('ad_002', 'user-002', 'ARTIST', 'ART-006', 'Shania Twain Comeback', 800.00, 800.00, 'DRAFT'),
       ('ad_003', 'user-003', 'PODCAST', 'pod_country_002', 'Nashville Insider Promo', 300.00, 120.00, 'ACTIVE'),
       ('ad_004', 'user-004', 'ARTIST', 'ART-010', 'Carrie Underwood Tour', 1000.00, 0.00, 'EXHAUSTED'),
       ('ad_005', 'user-005', 'PODCAST', 'pod_country_011', 'Outlaw Country Chronicles', 400.00, 200.00, 'PAUSED'),
       ('ad_006', 'user-006', 'ARTIST', 'ART-007', 'Chris Stapleton Spotlight', 600.00, 600.00, 'DRAFT'),
       ('ad_007', 'user-007', 'PODCAST', 'pod_country_005', 'Modern Country Monthly Boost', 250.00, 75.00, 'ACTIVE'),
       ('ad_008', 'user-001', 'ARTIST', 'ART-009', 'Luke Combs New Album', 700.00, 430.00, 'ACTIVE'),
       ('ad_009', 'user-002', 'PODCAST', 'pod_country_010', 'Women of Country Awareness', 350.00, 350.00, 'DRAFT'),
       ('ad_010', 'user-003', 'ARTIST', 'ART-003', 'Willie Nelson Legacy', 900.00, 480.00, 'PAUSED');

-- --------------------------------------------------------
-- ADDITIONAL DATA FOR TABLE: ad_creatives
-- Every ad (ad_001 to ad_010) gets at least 2 creatives
-- --------------------------------------------------------

-- Clean up existing specific creatives to avoid duplicates if re-running

INSERT INTO ad_creatives (ad_id, media_url, creative_type)
VALUES
-- ad_001
('ad_001', 'https://cdn.ads.com/johnny_cash/audio_30s.mp3', 'AUDIO_CLIP'),
('ad_001', 'https://cdn.ads.com/johnny_cash/banner.jpg', 'BANNER_IMAGE'),

-- ad_002
('ad_002', 'https://cdn.ads.com/shania_twain/banner.jpg', 'BANNER_IMAGE'),
('ad_002', 'https://cdn.ads.com/shania_twain/video.mp4', 'VIDEO_SPOT'),

-- ad_003
('ad_003', 'https://cdn.ads.com/nashville_insider/audio_intro.mp3', 'AUDIO_CLIP'),
('ad_003', 'https://cdn.ads.com/nashville_insider/banner.jpg', 'BANNER_IMAGE'),

-- ad_004
('ad_004', 'https://cdn.ads.com/carrie_underwood/video_tour.mp4', 'VIDEO_SPOT'),
('ad_004', 'https://cdn.ads.com/carrie_underwood/banner.jpg', 'BANNER_IMAGE'),

-- ad_005
('ad_005', 'https://cdn.ads.com/outlaw_chronicles/audio_trailer.mp3', 'AUDIO_CLIP'),
('ad_005', 'https://cdn.ads.com/outlaw_chronicles/banner.jpg', 'BANNER_IMAGE'),

-- ad_006
('ad_006', 'https://cdn.ads.com/chris_stapleton/audio_30s.mp3', 'AUDIO_CLIP'),
('ad_006', 'https://cdn.ads.com/chris_stapleton/banner.jpg', 'BANNER_IMAGE'),

-- ad_007
('ad_007', 'https://cdn.ads.com/modern_country/audio_ad.mp3', 'AUDIO_CLIP'),
('ad_007', 'https://cdn.ads.com/modern_country/video.mp4', 'VIDEO_SPOT'),

-- ad_008
('ad_008', 'https://cdn.ads.com/luke_combs/banner.jpg', 'BANNER_IMAGE'),
('ad_008', 'https://cdn.ads.com/luke_combs/video_album.mp4', 'VIDEO_SPOT'),

-- ad_009
('ad_009', 'https://cdn.ads.com/women_country/banner.jpg', 'BANNER_IMAGE'),
('ad_009', 'https://cdn.ads.com/women_country/audio_clip.mp3', 'AUDIO_CLIP'),

-- ad_010
('ad_010', 'https://cdn.ads.com/willie_nelson/audio_legacy.mp3', 'AUDIO_CLIP'),
('ad_010', 'https://cdn.ads.com/willie_nelson/banner.jpg', 'BANNER_IMAGE');


INSERT INTO targeting_rules (ad_id, target_genre, target_region, min_age)
VALUES
-- ad_001 (Johnny Cash)
('ad_001', 'OUTLAW', 'NORTH_AMERICA', 18),
('ad_001', 'HONKY_TONK', 'GLOBAL', 21),

-- ad_002 (Shania Twain)
('ad_002', 'COUNTRY_POP', 'GLOBAL', 16),
('ad_002', 'CANADIAN_COUNTRY', 'NORTH_AMERICA', 18),

-- ad_003 (Nashville Insider)
('ad_003', 'COUNTRY_POLITAN', 'NORTH_AMERICA', 18),
('ad_003', 'COUNTRY_POP', 'EUROPE', 18),

-- ad_004 (Carrie Underwood)
('ad_004', 'COUNTRY_POP', 'GLOBAL', 16),
('ad_004', 'COUNTRY_POLITAN', 'NORTH_AMERICA', 18),

-- ad_005 (Outlaw Country Chronicles)
('ad_005', 'OUTLAW', 'NORTH_AMERICA', 21),
('ad_005', 'ALTERNATIVE_COUNTRY', 'EUROPE', 21),

-- ad_006 (Chris Stapleton)
('ad_006', 'ALTERNATIVE_COUNTRY', 'GLOBAL', 18),
('ad_006', 'OUTLAW', 'NORTH_AMERICA', 21),

-- ad_007 (Modern Country Monthly)
('ad_007', 'COUNTRY_POP', 'GLOBAL', 16),
('ad_007', 'COUNTRY_POLITAN', 'NORTH_AMERICA', 18),

-- ad_008 (Luke Combs)
('ad_008', 'HONKY_TONK', 'NORTH_AMERICA', 18),
('ad_008', 'COUNTRY_POP', 'GLOBAL', 16),

-- ad_009 (Women of Country)
('ad_009', 'COUNTRY_POP', 'GLOBAL', 16),
('ad_009', 'CANADIAN_COUNTRY', 'NORTH_AMERICA', 18),

-- ad_010 (Willie Nelson)
('ad_010', 'OUTLAW', 'GLOBAL', 25),
('ad_010', 'ALTERNATIVE_COUNTRY', 'EUROPE', 21);
