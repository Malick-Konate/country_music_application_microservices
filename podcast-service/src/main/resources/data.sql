INSERT INTO podcast (podcast_id, title, hostname, description, pricing_model)
VALUES ('pod_country_001', 'Honky Tonk History', 'Clint Blackman',
        'Deep dives into the history of classic country music from the 1950s to the 1990s.', 'FREE'),
       ('pod_country_002', 'Nashville Insider', 'Sarah Twang',
        'Behind the scenes news and interviews from Music City USA.', 'SUBSCRIPTION'),
       ('pod_country_003', 'Bluegrass Breakdown', 'Earl Scruggs Jr.',
        'Exploring the fast-picking world of banjos, fiddles, and mandolins.', 'FREE'),
       ('pod_country_004', 'The Slide Guitar Hour', 'Jerry Douglas Fan',
        'Tutorials and appreciation for dobro and slide guitar techniques.', 'PER_EPISODE'),
       ('pod_country_005', 'Modern Country Monthly', 'Taylor Shelton',
        'Reviews and discussions on the latest hits topping the Billboard Country charts.', 'FREE'),
       ('pod_country_006', 'Legends of the Grand Ole Opry', 'Opry Fanatic',
        'Stories about the most legendary performances on the most famous stage in country music.', 'SUBSCRIPTION'),
       ('pod_country_007', 'Texas Red Dirt Radio', 'Pat Greenish',
        'Focusing on the gritty, independent country music scene from Texas and Oklahoma.', 'FREE'),
       ('pod_country_008', 'Songwriters Circle', 'Kris Kristofferson AI',
        'Deconstructing the lyrics and composition of the greatest country songs ever written.', 'PER_EPISODE'),
       ('pod_country_009', 'Banjo & Bourbon', 'The Kentucky Boys',
        'Casual conversations about roots music over a glass of fine bourbon.', 'FREE'),
       ('pod_country_010', 'Women of Country', 'Dolly Parton Tribute',
        'Celebrating the female trailblazers of country music from Patsy Cline to Miranda Lambert.', 'FREE'),
       ('pod_country_011', 'Outlaw Country Chronicles', 'Waylon Willie',
        'Tales from the rebellious side of country music featuring Willie, Waylon, and Merle.', 'SUBSCRIPTION'),
       ('pod_country_012', 'Folk & Americana Roots', 'John Prine Spirit',
        'Exploring the softer, storytelling side of American roots music.', 'FREE'),
       ('pod_country_013', 'Truckers Tunes', 'Big Rig Rob',
        'The best driving songs for long hauls across the interstate.', 'PER_EPISODE'),
       ('pod_country_014', 'Gospel Country Sunday', 'Reverend Cash',
        'Uplifting country gospel music for your Sunday morning.', 'FREE'),
       ('pod_country_015', 'Steel Guitar Secrets', 'Paul Franklin Fan',
        'Technical deep dives into pedal steel guitar mechanics and playing styles.', 'SUBSCRIPTION'),
       ('pod_country_016', 'Canadian Country Spotlight', 'Northern Nashville',
        'Highlighting the biggest names and rising stars in Canadian country music.', 'FREE'),

       ('pod_country_017', 'Country Song Breakdown', 'Studio Session Sam',
        'Breaking down the production, lyrics, and songwriting techniques behind today’s biggest country hits.',
        'SUBSCRIPTION'),

       ('pod_country_018', 'Backstage at the Opry', 'Southern Stage Mike',
        'Exclusive backstage-style storytelling from legendary country venues and festivals.', 'PER_EPISODE');
-- --------------------------------------------------------
-- DATA FOR TABLE: episode
-- Theme: Country Music Deep Dives
-- --------------------------------------------------------

INSERT INTO episode (episode_id, podcast_id, title, duration, publish_date, status)
VALUES
-- Episodes for pod_country_001 (Honky Tonk History)
('ep_001', 'pod_country_001', 'The Rise of Hank Williams', '00:45:00', '2023-01-15', 'PUBLISHED'),
('ep_002', 'pod_country_001', 'Johnny Cash at Folsom Prison: The Full Story', '01:10:00', '2023-02-01', 'PUBLISHED'),
('ep_003', 'pod_country_001', 'The Outlaw Movement: Waylon and Willie', '00:55:00', '2023-02-15', 'PUBLISHED'),
('ep_004', 'pod_country_001', 'Classic Duets: George and Tammy', '00:50:00', '2023-03-01', 'PUBLISHED'),

-- Episodes for pod_country_002 (Nashville Insider)
('ep_005', 'pod_country_002', 'Exclusive: Interview with Carrie Underwoods Producer', '00:30:00', '2023-10-12',
 'PUBLISHED'),
('ep_006', 'pod_country_002', 'The Future of the Grand Ole Opry', '00:35:00', '2023-10-19', 'PUBLISHED'),
('ep_007', 'pod_country_002', 'CMA Awards: Predictions and Snubs', '00:40:00', '2023-10-26', 'PUBLISHED'),

-- Episodes for pod_country_003 (Bluegrass Breakdown)
('ep_008', 'pod_country_003', 'Bill Monroe: The Father of Bluegrass', '00:55:00', '2023-05-20', 'PUBLISHED'),
('ep_009', 'pod_country_003', 'Fiddle vs. Violin: The Great Debate', '00:40:00', '2023-06-15', 'PUBLISHED'),
('ep_010', 'pod_country_003', 'Modern Bluegrass: The Billy Strings Effect', '01:05:00', '2023-07-01', 'PUBLISHED'),
('ep_011', 'pod_country_003', 'Mastering the Scruggs Style Banjo', '00:50:00', '2023-07-15', 'PUBLISHED'),

-- Episodes for pod_country_004 (The Slide Guitar Hour)
('ep_012', 'pod_country_004', 'Mastering Open G Tuning', '00:25:00', '2023-11-05', 'PUBLISHED'),
('ep_013', 'pod_country_004', 'Dobro Legends: Josh Graves to Jerry Douglas', '00:45:00', '2023-11-12', 'PUBLISHED'),
('ep_014', 'pod_country_004', 'Lap Steel Basics for Beginners', '00:30:00', '2023-11-19', 'PUBLISHED'),

-- Episodes for pod_country_005 (Modern Country Monthly)
('ep_015', 'pod_country_005', 'Review: The New Luke Combs Album', '00:45:00', '2023-09-01', 'PUBLISHED'),
('ep_016', 'pod_country_005', 'Morgan Wallen and the Streaming Era', '00:50:00', '2023-09-15', 'PUBLISHED'),
('ep_017', 'pod_country_005', 'The Evolution of Country-Pop', '00:40:00', '2023-10-01', 'PUBLISHED'),
('ep_018', 'pod_country_005', 'Rising Stars: Who to Watch in 2024', '00:35:00', '2023-11-01', 'PUBLISHED'),

-- pod_country_006 (Legends of the Grand Ole Opry)
('ep_019', 'pod_country_006', 'Minnie Pearls Best Jokes', '00:20:00', '2023-03-10', 'ARCHIVED'),
('ep_020', 'pod_country_006', 'The Night Hank Williams Was Banned', '00:55:00', '2023-03-17', 'PUBLISHED'),
('ep_021', 'pod_country_006', 'Dolly Partons Opry Debut', '00:45:00', '2023-03-24', 'PUBLISHED'),

-- pod_country_007 (Texas Red Dirt Radio)
('ep_022', 'pod_country_007', 'Why Texas Country is Different from Nashville', '01:00:00', '2023-07-04', 'PUBLISHED'),
('ep_023', 'pod_country_007', 'Live from Gruene Hall: Robert Earl Keen', '01:15:00', '2023-07-11', 'PUBLISHED'),
('ep_024', 'pod_country_007', 'The Red Dirt Revolution in Oklahoma', '00:50:00', '2023-07-18', 'PUBLISHED'),
('ep_025', 'pod_country_007', 'Acoustic Session: Cody Johnson', '00:40:00', '2023-07-25', 'PUBLISHED'),

-- pod_country_008 (Songwriters Circle)
('ep_026', 'pod_country_008', 'Deconstructing The Gambler by Kenny Rogers', '00:50:00', '2023-08-15', 'PUBLISHED'),
('ep_027', 'pod_country_008', 'The Art of the Story Song', '00:45:00', '2023-08-22', 'PUBLISHED'),
('ep_028', 'pod_country_008', 'Guy Clark: The Craftsman of Country', '01:05:00', '2023-08-29', 'PUBLISHED'),

-- pod_country_009 (Banjo & Bourbon)
('ep_029', 'pod_country_009', 'Old-Time vs. Three-Finger Picking', '00:55:00', '2023-09-05', 'PUBLISHED'),
('ep_030', 'pod_country_009', 'Kentucky Bourbon Tasting with Bela Fleck', '01:10:00', '2023-09-12', 'PUBLISHED'),
('ep_031', 'pod_country_009', 'The History of the Clawhammer Style', '00:45:00', '2023-09-19', 'PUBLISHED'),

-- pod_country_010 (Women of Country)
('ep_032', 'pod_country_010', 'Patsy Cline: Walking After Midnight', '00:35:00', '2023-04-22', 'PUBLISHED'),
('ep_033', 'pod_country_010', 'Loretta Lynn: The Coal Miners Daughter', '00:50:00', '2023-04-29', 'PUBLISHED'),
('ep_034', 'pod_country_010', 'The Highwomen: A New Era', '00:45:00', '2023-05-06', 'PUBLISHED'),
('ep_035', 'pod_country_010', 'Tammy Wynette and the First Lady of Country', '00:40:00', '2023-05-13', 'PUBLISHED'),

-- pod_country_011 (Outlaw Country Chronicles)
('ep_036', 'pod_country_011', 'The Story of Willie Nelsons Guitar Trigger', '00:48:00', '2023-12-01', 'PUBLISHED'),
('ep_037', 'pod_country_011', 'Waylon Jennings: Dreaming My Dreams', '00:55:00', '2023-12-08', 'PUBLISHED'),
('ep_038', 'pod_country_011', 'Merle Haggard: Mama Tried', '01:00:00', '2023-12-15', 'PUBLISHED'),

-- pod_country_012 (Folk & Americana Roots)
('ep_039', 'pod_country_012', 'The Carter Family Tree Explained', '01:15:00', '2023-02-28', 'PUBLISHED'),
('ep_040', 'pod_country_012', 'Woody Guthrie: This Land is Your Land', '00:45:00', '2023-03-07', 'PUBLISHED'),
('ep_041', 'pod_country_012', 'The Newport Folk Festival Scandal', '00:50:00', '2023-03-14', 'PUBLISHED'),

-- pod_country_013 (Truckers Tunes)
('ep_042', 'pod_country_013', 'Top 10 Songs for a Cross-Country Drive', '00:42:00', '2023-11-20', 'PUBLISHED'),
('ep_043', 'pod_country_013', 'Six Days on the Road: History of a Classic', '00:30:00', '2023-11-27', 'PUBLISHED'),
('ep_044', 'pod_country_013', 'Jerry Reed: The Snowman of Country Music', '00:45:00', '2023-12-04', 'PUBLISHED'),
('ep_045', 'pod_country_013', 'The Best Truck Stops and Radio Stations', '00:50:00', '2023-12-11', 'PUBLISHED'),

-- pod_country_014 (Gospel Country Sunday)
('ep_046', 'pod_country_014', 'The Gospel Side of Elvis Presley', '00:40:00', '2024-01-07', 'PUBLISHED'),
('ep_047', 'pod_country_014', 'Old Rugged Cross: Evolution of a Hymn', '00:35:00', '2024-01-14', 'PUBLISHED'),
('ep_048', 'pod_country_014', 'Bluegrass Gospel Essentials', '00:45:00', '2024-01-21', 'PUBLISHED'),

-- pod_country_015 (Steel Guitar Secrets)
('ep_049', 'pod_country_015', 'Pedal Steel Maintenance 101', '00:28:00', '2023-10-30', 'PUBLISHED'),
('ep_050', 'pod_country_015', 'The C6 vs E9 Tuning Explained', '01:05:00', '2023-11-06', 'PUBLISHED'),
('ep_051', 'pod_country_015', 'How to Play a Cryin Steel Lick', '00:40:00', '2023-11-13', 'PUBLISHED'),
('ep_052', 'pod_country_015', 'Interview: Modern Steel Legend Buddy Emmons', '01:20:00', '2023-11-20',
 'PUBLISHED'), -- --------------------------------------------------------
-- pod_country_016 (Canadian Country Spotlight)
-- --------------------------------------------------------
('ep_053', 'pod_country_016', 'The Rise of MacKenzie Porter', '00:42:00', '2024-02-01', 'PUBLISHED'),
('ep_054', 'pod_country_016', 'The Reklaws and the New Party Country Wave', '00:38:00', '2024-02-08', 'PUBLISHED'),
('ep_055', 'pod_country_016', 'Madeline Merlo: From BC to Nashville', '00:35:00', '2024-02-15', 'PUBLISHED'),
('ep_056', 'pod_country_016', 'Top 10 Canadian Country Songs of 2024', '00:50:00', '2024-02-22', 'PUBLISHED'),
('ep_057', 'pod_country_016', 'How Canada Shapes Modern Country Music', '00:45:00', '2024-03-01', 'PUBLISHED'),

-- --------------------------------------------------------
-- pod_country_017 (Country Song Breakdown)
-- --------------------------------------------------------
('ep_058', 'pod_country_017', 'Inside the Production of a Luke Combs Hit', '00:55:00', '2024-03-05', 'PUBLISHED'),
('ep_059', 'pod_country_017', 'The Secret Formula of Country-Pop Hooks', '00:48:00', '2024-03-12', 'PUBLISHED'),
('ep_060', 'pod_country_017', 'Writing the Perfect Country Love Song', '00:40:00', '2024-03-19', 'PUBLISHED'),
('ep_061', 'pod_country_017', 'Acoustic vs. Full Band: Arrangement Choices', '00:46:00', '2024-03-26', 'PUBLISHED'),
('ep_062', 'pod_country_017', 'Why Storytelling Still Wins in 2024', '00:52:00', '2024-04-02', 'PUBLISHED'),

-- --------------------------------------------------------
-- pod_country_018 (Backstage at the Opry)
-- --------------------------------------------------------
('ep_063', 'pod_country_018', 'Behind the Curtains of the Grand Ole Opry', '01:05:00', '2024-04-10', 'PUBLISHED'),
('ep_064', 'pod_country_018', 'Stage Fright: Confessions from Touring Artists', '00:50:00', '2024-04-17', 'PUBLISHED'),
('ep_065', 'pod_country_018', 'Festival Life: CMA Fest Stories', '00:45:00', '2024-04-24', 'PUBLISHED'),
('ep_066', 'pod_country_018', 'Soundcheck Secrets from Nashville Studios', '00:38:00', '2024-05-01', 'PUBLISHED'),
('ep_067', 'pod_country_018', 'The Evolution of Live Country Performances', '00:55:00', '2024-05-08', 'PUBLISHED');

