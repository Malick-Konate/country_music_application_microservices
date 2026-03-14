INSERT INTO album (album_id, artist_id, title, release_date, record_label, album_type)
VALUES ('ALB-001', 'ART-001', 'Whiskey Roads', '2021-03-15', 'Lone Star Records', 'LP'),
       ('ALB-002', 'ART-002', 'Southern Skies', '2020-06-01', 'Skyline Sounds', 'EP'),
       ('ALB-003', 'ART-003', 'Heartland Echoes', '2022-11-20', 'Dusty Trail', 'SINGLE'),
       ('ALB-004', 'ART-004', 'Cowboy Sunset', '2023-01-10', 'Gold Boot Records', 'LP'),
       ('ALB-005', 'ART-005', 'Maple Leaf Dreams', '2019-09-05', 'Front Porch Sound', 'EP'),
       ('ALB-006', 'ART-006', 'Gravel and Grace', '2022-07-17', 'Prairie Tunes', 'LP'),
       ('ALB-007', 'ART-007', 'Steel Strings', '2020-12-24', 'Outlaw Harmony', 'EP'),
       ('ALB-008', 'ART-008', 'Dust Bowl Diaries', '2021-05-30', 'Ramblin’ Soul', 'SINGLE'),
       ('ALB-009', 'ART-009', 'Neon Honky Tonk', '2018-08-19', 'Blue Ridge Beats', 'LP'),
       ('ALB-010', 'ART-010', 'Moonshine Lullabies', '2023-03-03', 'Wild West Vibes', 'EP'),
       -- Alana Springsteen
       ('ALB-011', 'ART-011', 'TWENTY SOMETHING', '2023-05-05', 'Columbia Records', 'LP'),

-- Megan Patrick
       ('ALB-012', 'ART-012', 'Greatest Show on Dirt', '2024-03-15', 'Riser House Records', 'LP'),

-- Robyn Ottolini
       ('ALB-013', 'ART-013', 'Growing Up to Do', '2023-09-22', 'Warner Music Canada', 'LP'),
       -- The Reklaws
       ('ALB-014', 'ART-014', 'Good Ol’ Days', '2022-11-04', 'Universal Music Canada', 'LP'),

-- Madeline Merlo
       ('ALB-015', 'ART-015', 'Slide', '2023-03-10', 'Open Road Recordings', 'EP'),

-- MacKenzie Porter
       ('ALB-016', 'ART-016', 'Nobody’s Born With a Broken Heart', '2022-04-08', 'Big Loud Records', 'LP');



INSERT INTO song (album_id, title, duration, lyrics)
VALUES
-- Songs for Johnny Cash's "Whiskey Roads"
('ALB-001', 'Highway of Shadows', '00:03:45', 'Lyrics for Highway of Shadows...'),
('ALB-001', 'Whiskey Roads', '00:04:12', 'Lyrics for Whiskey Roads...'),

-- Songs for Dolly Parton's "Southern Skies"
('ALB-002', 'Southern Skies', '00:02:58', 'Lyrics for Southern Skies...'),
('ALB-002', 'Morning Light', '00:03:21', 'Lyrics for Morning Light...'),

-- Songs for Willie Nelson's "Heartland Echoes"
('ALB-003', 'Echoes of Home', '00:04:05', 'Lyrics for Echoes of Home...'),

-- Songs for Reba McEntire's "Cowboy Sunset"
('ALB-004', 'Cowboy Sunset', '00:03:50', 'Lyrics for Cowboy Sunset...'),
('ALB-004', 'Ride Into the Night', '00:04:10', 'Lyrics for Ride Into the Night...'),

-- Songs for Garth Brooks' "Maple Leaf Dreams"
('ALB-005', 'Dreams of the Maple', '00:03:15', 'Lyrics for Dreams of the Maple...'),

-- Songs for Shania Twain's "Gravel and Grace"
('ALB-006', 'Gravel Roads', '00:03:55', 'Lyrics for Gravel Roads...'),
('ALB-006', 'Graceful Nights', '00:04:20', 'Lyrics for Graceful Nights...'),

-- Songs for Chris Stapleton's "Steel Strings"
('ALB-007', 'Steel Strings', '00:03:40', 'Lyrics for Steel Strings...'),

-- Songs for Kacey Musgraves' "Dust Bowl Diaries"
('ALB-008', 'Dust Bowl Dreams', '00:03:35', 'Lyrics for Dust Bowl Dreams...'),

-- Songs for Luke Combs' "Neon Honky Tonk"
('ALB-009', 'Neon Nights', '00:04:00', 'Lyrics for Neon Nights...'),
('ALB-009', 'Honky Tonk Heart', '00:03:30', 'Lyrics for Honky Tonk Heart...'),

-- Songs for Carrie Underwood's "Moonshine Lullabies"
('ALB-010', 'Moonshine Lullaby', '00:03:45', 'Lyrics for Moonshine Lullaby...'),
-- Alana Springsteen - TWENTY SOMETHING
('ALB-011', 'goodbye looks good on you', '00:03:12', 'Lyrics for goodbye looks good on you...'),
('ALB-011', 'you don’t deserve a country song', '00:03:08', 'Lyrics for you don’t deserve a country song...'),
('ALB-011', 'ghost in my guitar', '00:03:30', 'Lyrics for ghost in my guitar...'),

-- Megan Patrick - Greatest Show on Dirt
('ALB-012', 'Greatest Show on Dirt', '00:03:25', 'Lyrics for Greatest Show on Dirt...'),
('ALB-012', 'She’s No Good for Me', '00:03:18', 'Lyrics for She’s No Good for Me...'),

-- Robyn Ottolini - Growing Up to Do
('ALB-013', 'F-150', '00:03:22', 'Lyrics for F-150...'),
('ALB-013', 'Say It', '00:03:05', 'Lyrics for Say It...'),
-- The Reklaws - Good Ol’ Days
('ALB-014', 'Good Ol’ Days', '00:03:12', 'Lyrics for Good Ol’ Days...'),
('ALB-014', 'What the Truck', '00:02:55', 'Lyrics for What the Truck...'),

-- Madeline Merlo - Slide
('ALB-015', 'Slide', '00:03:05', 'Lyrics for Slide...'),
('ALB-015', 'Same Car', '00:03:18', 'Lyrics for Same Car...'),

-- MacKenzie Porter - Nobody’s Born With a Broken Heart
('ALB-016', 'Pickup', '00:03:22', 'Lyrics for Pickup...'),
('ALB-016', 'These Days', '00:03:30', 'Lyrics for These Days...');


