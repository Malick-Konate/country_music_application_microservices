// Switch to the podcast database
db = db.getSiblingDB('podcast');

// // 1. Create Collections
// db.createCollection('podcasts');
// db.createCollection('episodes');

// 2. Create Indexes
db.podcasts.createIndex({ "podcastId": 1 }, { unique: true });
db.episodes.createIndex({ "episodeId": 1 }, { unique: true });
db.episodes.createIndex({ "podcastId": 1 });

// 3. Insert Podcast Data
db.podcasts.insertMany([
    { "podcastId": "pod_country_001", "title": "Honky Tonk History", "hostname": "Clint Blackman", "description": "Deep dives into the history of classic country music from the 1950s to the 1990s.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_002", "title": "Nashville Insider", "hostname": "Sarah Twang", "description": "Behind the scenes news and interviews from Music City USA.", "pricingModel": "SUBSCRIPTION" },
    { "podcastId": "pod_country_003", "title": "Bluegrass Breakdown", "hostname": "Earl Scruggs Jr.", "description": "Exploring the fast-picking world of banjos, fiddles, and mandolins.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_004", "title": "The Slide Guitar Hour", "hostname": "Jerry Douglas Fan", "description": "Tutorials and appreciation for dobro and slide guitar techniques.", "pricingModel": "PER_EPISODE" },
    { "podcastId": "pod_country_005", "title": "Modern Country Monthly", "hostname": "Taylor Shelton", "description": "Reviews and discussions on the latest hits topping the Billboard Country charts.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_006", "title": "Legends of the Grand Ole Opry", "hostname": "Opry Fanatic", "description": "Stories about the most legendary performances on the most famous stage in country music.", "pricingModel": "SUBSCRIPTION" },
    { "podcastId": "pod_country_007", "title": "Texas Red Dirt Radio", "hostname": "Pat Greenish", "description": "Focusing on the gritty, independent country music scene from Texas and Oklahoma.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_008", "title": "Songwriters Circle", "hostname": "Kris Kristofferson AI", "description": "Deconstructing the lyrics and composition of the greatest country songs ever written.", "pricingModel": "PER_EPISODE" },
    { "podcastId": "pod_country_009", "title": "Banjo & Bourbon", "hostname": "The Kentucky Boys", "description": "Casual conversations about roots music over a glass of fine bourbon.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_010", "title": "Women of Country", "hostname": "Dolly Parton Tribute", "description": "Celebrating the female trailblazers of country music from Patsy Cline to Miranda Lambert.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_011", "title": "Outlaw Country Chronicles", "hostname": "Waylon Willie", "description": "Tales from the rebellious side of country music featuring Willie, Waylon, and Merle.", "pricingModel": "SUBSCRIPTION" },
    { "podcastId": "pod_country_012", "title": "Folk & Americana Roots", "hostname": "John Prine Spirit", "description": "Exploring the softer, storytelling side of American roots music.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_013", "title": "Truckers Tunes", "hostname": "Big Rig Rob", "description": "The best driving songs for long hauls across the interstate.", "pricingModel": "PER_EPISODE" },
    { "podcastId": "pod_country_014", "title": "Gospel Country Sunday", "hostname": "Reverend Cash", "description": "Uplifting country gospel music for your Sunday morning.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_015", "title": "Steel Guitar Secrets", "hostname": "Paul Franklin Fan", "description": "Technical deep dives into pedal steel guitar mechanics and playing styles.", "pricingModel": "SUBSCRIPTION" },
    { "podcastId": "pod_country_016", "title": "Canadian Country Spotlight", "hostname": "Northern Nashville", "description": "Highlighting the biggest names and rising stars in Canadian country music.", "pricingModel": "FREE" },
    { "podcastId": "pod_country_017", "title": "Country Song Breakdown", "hostname": "Studio Session Sam", "description": "Breaking down the production, lyrics, and songwriting techniques behind today’s biggest country hits.", "pricingModel": "SUBSCRIPTION" },
    { "podcastId": "pod_country_018", "title": "Backstage at the Opry", "hostname": "Southern Stage Mike", "description": "Exclusive backstage-style storytelling from legendary country venues and festivals.", "pricingModel": "PER_EPISODE" }
]);

// 4. Insert ALL Episode Data
db.episodes.insertMany([
    { "episodeId": "ep_001", "podcastId": "pod_country_001", "title": "The Rise of Hank Williams", "duration": "00:45:00", "publishDate": new Date("2023-01-15"), "status": "PUBLISHED" },
    { "episodeId": "ep_002", "podcastId": "pod_country_001", "title": "Johnny Cash at Folsom Prison: The Full Story", "duration": "01:10:00", "publishDate": new Date("2023-02-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_003", "podcastId": "pod_country_001", "title": "The Outlaw Movement: Waylon and Willie", "duration": "00:55:00", "publishDate": new Date("2023-02-15"), "status": "PUBLISHED" },
    { "episodeId": "ep_004", "podcastId": "pod_country_001", "title": "Classic Duets: George and Tammy", "duration": "00:50:00", "publishDate": new Date("2023-03-01"), "status": "PUBLISHED" },

    { "episodeId": "ep_005", "podcastId": "pod_country_002", "title": "Exclusive: Interview with Carrie Underwoods Producer", "duration": "00:30:00", "publishDate": new Date("2023-10-12"), "status": "PUBLISHED" },
    { "episodeId": "ep_006", "podcastId": "pod_country_002", "title": "The Future of the Grand Ole Opry", "duration": "00:35:00", "publishDate": new Date("2023-10-19"), "status": "PUBLISHED" },
    { "episodeId": "ep_007", "podcastId": "pod_country_002", "title": "CMA Awards: Predictions and Snubs", "duration": "00:40:00", "publishDate": new Date("2023-10-26"), "status": "PUBLISHED" },

    { "episodeId": "ep_008", "podcastId": "pod_country_003", "title": "Bill Monroe: The Father of Bluegrass", "duration": "00:55:00", "publishDate": new Date("2023-05-20"), "status": "PUBLISHED" },
    { "episodeId": "ep_009", "podcastId": "pod_country_003", "title": "Fiddle vs. Violin: The Great Debate", "duration": "00:40:00", "publishDate": new Date("2023-06-15"), "status": "PUBLISHED" },
    { "episodeId": "ep_010", "podcastId": "pod_country_003", "title": "Modern Bluegrass: The Billy Strings Effect", "duration": "01:05:00", "publishDate": new Date("2023-07-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_011", "podcastId": "pod_country_003", "title": "Mastering the Scruggs Style Banjo", "duration": "00:50:00", "publishDate": new Date("2023-07-15"), "status": "PUBLISHED" },

    { "episodeId": "ep_012", "podcastId": "pod_country_004", "title": "Mastering Open G Tuning", "duration": "00:25:00", "publishDate": new Date("2023-11-05"), "status": "PUBLISHED" },
    { "episodeId": "ep_013", "podcastId": "pod_country_004", "title": "Dobro Legends: Josh Graves to Jerry Douglas", "duration": "00:45:00", "publishDate": new Date("2023-11-12"), "status": "PUBLISHED" },
    { "episodeId": "ep_014", "podcastId": "pod_country_004", "title": "Lap Steel Basics for Beginners", "duration": "00:30:00", "publishDate": new Date("2023-11-19"), "status": "PUBLISHED" },

    { "episodeId": "ep_015", "podcastId": "pod_country_005", "title": "Review: The New Luke Combs Album", "duration": "00:45:00", "publishDate": new Date("2023-09-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_016", "podcastId": "pod_country_005", "title": "Morgan Wallen and the Streaming Era", "duration": "00:50:00", "publishDate": new Date("2023-09-15"), "status": "PUBLISHED" },
    { "episodeId": "ep_017", "podcastId": "pod_country_005", "title": "The Evolution of Country-Pop", "duration": "00:40:00", "publishDate": new Date("2023-10-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_018", "podcastId": "pod_country_005", "title": "Rising Stars: Who to Watch in 2024", "duration": "00:35:00", "publishDate": new Date("2023-11-01"), "status": "PUBLISHED" },

    { "episodeId": "ep_019", "podcastId": "pod_country_006", "title": "Minnie Pearls Best Jokes", "duration": "00:20:00", "publishDate": new Date("2023-03-10"), "status": "ARCHIVED" },
    { "episodeId": "ep_020", "podcastId": "pod_country_006", "title": "The Night Hank Williams Was Banned", "duration": "00:55:00", "publishDate": new Date("2023-03-17"), "status": "PUBLISHED" },
    { "episodeId": "ep_021", "podcastId": "pod_country_006", "title": "Dolly Partons Opry Debut", "duration": "00:45:00", "publishDate": new Date("2023-03-24"), "status": "PUBLISHED" },

    { "episodeId": "ep_022", "podcastId": "pod_country_007", "title": "Why Texas Country is Different from Nashville", "duration": "01:00:00", "publishDate": new Date("2023-07-04"), "status": "PUBLISHED" },
    { "episodeId": "ep_023", "podcastId": "pod_country_007", "title": "Live from Gruene Hall: Robert Earl Keen", "duration": "01:15:00", "publishDate": new Date("2023-07-11"), "status": "PUBLISHED" },
    { "episodeId": "ep_024", "podcastId": "pod_country_007", "title": "The Red Dirt Revolution in Oklahoma", "duration": "00:50:00", "publishDate": new Date("2023-07-18"), "status": "PUBLISHED" },
    { "episodeId": "ep_025", "podcastId": "pod_country_007", "title": "Acoustic Session: Cody Johnson", "duration": "00:40:00", "publishDate": new Date("2023-07-25"), "status": "PUBLISHED" },

    { "episodeId": "ep_026", "podcastId": "pod_country_008", "title": "Deconstructing The Gambler by Kenny Rogers", "duration": "00:50:00", "publishDate": new Date("2023-08-15"), "status": "PUBLISHED" },
    { "episodeId": "ep_027", "podcastId": "pod_country_008", "title": "The Art of the Story Song", "duration": "00:45:00", "publishDate": new Date("2023-08-22"), "status": "PUBLISHED" },
    { "episodeId": "ep_028", "podcastId": "pod_country_008", "title": "Guy Clark: The Craftsman of Country", "duration": "01:05:00", "publishDate": new Date("2023-08-29"), "status": "PUBLISHED" },

    { "episodeId": "ep_029", "podcastId": "pod_country_009", "title": "Old-Time vs. Three-Finger Picking", "duration": "00:55:00", "publishDate": new Date("2023-09-05"), "status": "PUBLISHED" },
    { "episodeId": "ep_030", "podcastId": "pod_country_009", "title": "Kentucky Bourbon Tasting with Bela Fleck", "duration": "01:10:00", "publishDate": new Date("2023-09-12"), "status": "PUBLISHED" },
    { "episodeId": "ep_031", "podcastId": "pod_country_009", "title": "The History of the Clawhammer Style", "duration": "00:45:00", "publishDate": new Date("2023-09-19"), "status": "PUBLISHED" },

    { "episodeId": "ep_032", "podcastId": "pod_country_010", "title": "Patsy Cline: Walking After Midnight", "duration": "00:35:00", "publishDate": new Date("2023-04-22"), "status": "PUBLISHED" },
    { "episodeId": "ep_033", "podcastId": "pod_country_010", "title": "Loretta Lynn: The Coal Miners Daughter", "duration": "00:50:00", "publishDate": new Date("2023-04-29"), "status": "PUBLISHED" },
    { "episodeId": "ep_034", "podcastId": "pod_country_010", "title": "The Highwomen: A New Era", "duration": "00:45:00", "publishDate": new Date("2023-05-06"), "status": "PUBLISHED" },
    { "episodeId": "ep_035", "podcastId": "pod_country_010", "title": "Tammy Wynette and the First Lady of Country", "duration": "00:40:00", "publishDate": new Date("2023-05-13"), "status": "PUBLISHED" },

    { "episodeId": "ep_036", "podcastId": "pod_country_011", "title": "The Story of Willie Nelsons Guitar Trigger", "duration": "00:48:00", "publishDate": new Date("2023-12-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_037", "podcastId": "pod_country_011", "title": "Waylon Jennings: Dreaming My Dreams", "duration": "00:55:00", "publishDate": new Date("2023-12-08"), "status": "PUBLISHED" },
    { "episodeId": "ep_038", "podcastId": "pod_country_011", "title": "Merle Haggard: Mama Tried", "duration": "01:00:00", "publishDate": new Date("2023-12-15"), "status": "PUBLISHED" },

    { "episodeId": "ep_039", "podcastId": "pod_country_012", "title": "The Carter Family Tree Explained", "duration": "01:15:00", "publishDate": new Date("2023-02-28"), "status": "PUBLISHED" },
    { "episodeId": "ep_040", "podcastId": "pod_country_012", "title": "Woody Guthrie: This Land is Your Land", "duration": "00:45:00", "publishDate": new Date("2023-03-07"), "status": "PUBLISHED" },
    { "episodeId": "ep_041", "podcastId": "pod_country_012", "title": "The Newport Folk Festival Scandal", "duration": "00:50:00", "publishDate": new Date("2023-03-14"), "status": "PUBLISHED" },

    { "episodeId": "ep_042", "podcastId": "pod_country_013", "title": "Top 10 Songs for a Cross-Country Drive", "duration": "00:42:00", "publishDate": new Date("2023-11-20"), "status": "PUBLISHED" },
    { "episodeId": "ep_043", "podcastId": "pod_country_013", "title": "Six Days on the Road: History of a Classic", "duration": "00:30:00", "publishDate": new Date("2023-11-27"), "status": "PUBLISHED" },
    { "episodeId": "ep_044", "podcastId": "pod_country_013", "title": "Jerry Reed: The Snowman of Country Music", "duration": "00:45:00", "publishDate": new Date("2023-12-04"), "status": "PUBLISHED" },
    { "episodeId": "ep_045", "podcastId": "pod_country_013", "title": "The Best Truck Stops and Radio Stations", "duration": "00:50:00", "publishDate": new Date("2023-12-11"), "status": "PUBLISHED" },

    { "episodeId": "ep_046", "podcastId": "pod_country_014", "title": "The Gospel Side of Elvis Presley", "duration": "00:40:00", "publishDate": new Date("2024-01-07"), "status": "PUBLISHED" },
    { "episodeId": "ep_047", "podcastId": "pod_country_014", "title": "Old Rugged Cross: Evolution of a Hymn", "duration": "00:35:00", "publishDate": new Date("2024-01-14"), "status": "PUBLISHED" },
    { "episodeId": "ep_048", "podcastId": "pod_country_014", "title": "Bluegrass Gospel Essentials", "duration": "00:45:00", "publishDate": new Date("2024-01-21"), "status": "PUBLISHED" },

    { "episodeId": "ep_049", "podcastId": "pod_country_015", "title": "Pedal Steel Maintenance 101", "duration": "00:28:00", "publishDate": new Date("2023-10-30"), "status": "PUBLISHED" },
    { "episodeId": "ep_050", "podcastId": "pod_country_015", "title": "The C6 vs E9 Tuning Explained", "duration": "01:05:00", "publishDate": new Date("2023-11-06"), "status": "PUBLISHED" },
    { "episodeId": "ep_051", "podcastId": "pod_country_015", "title": "How to Play a Cryin Steel Lick", "duration": "00:40:00", "publishDate": new Date("2023-11-13"), "status": "PUBLISHED" },
    { "episodeId": "ep_052", "podcastId": "pod_country_015", "title": "Interview: Modern Steel Legend Buddy Emmons", "duration": "01:20:00", "publishDate": new Date("2023-11-20"), "status": "PUBLISHED" },

    { "episodeId": "ep_053", "podcastId": "pod_country_016", "title": "The Rise of MacKenzie Porter", "duration": "00:42:00", "publishDate": new Date("2024-02-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_054", "podcastId": "pod_country_016", "title": "The Reklaws and the New Party Country Wave", "duration": "00:38:00", "publishDate": new Date("2024-02-08"), "status": "PUBLISHED" },
    { "episodeId": "ep_055", "podcastId": "pod_country_016", "title": "Madeline Merlo: From BC to Nashville", "duration": "00:35:00", "publishDate": new Date("2024-02-15"), "status": "PUBLISHED" },
    { "episodeId": "ep_056", "podcastId": "pod_country_016", "title": "Top 10 Canadian Country Songs of 2024", "duration": "00:50:00", "publishDate": new Date("2024-02-22"), "status": "PUBLISHED" },
    { "episodeId": "ep_057", "podcastId": "pod_country_016", "title": "How Canada Shapes Modern Country Music", "duration": "00:45:00", "publishDate": new Date("2024-03-01"), "status": "PUBLISHED" },

    { "episodeId": "ep_058", "podcastId": "pod_country_017", "title": "Inside the Production of a Luke Combs Hit", "duration": "00:55:00", "publishDate": new Date("2024-03-05"), "status": "PUBLISHED" },
    { "episodeId": "ep_059", "podcastId": "pod_country_017", "title": "The Secret Formula of Country-Pop Hooks", "duration": "00:48:00", "publishDate": new Date("2024-03-12"), "status": "PUBLISHED" },
    { "episodeId": "ep_060", "podcastId": "pod_country_017", "title": "Writing the Perfect Country Love Song", "duration": "00:40:00", "publishDate": new Date("2024-03-19"), "status": "PUBLISHED" },
    { "episodeId": "ep_061", "podcastId": "pod_country_017", "title": "Acoustic vs. Full Band: Arrangement Choices", "duration": "00:46:00", "publishDate": new Date("2024-03-26"), "status": "PUBLISHED" },
    { "episodeId": "ep_062", "podcastId": "pod_country_017", "title": "Why Storytelling Still Wins in 2024", "duration": "00:52:00", "publishDate": new Date("2024-04-02"), "status": "PUBLISHED" },

    { "episodeId": "ep_063", "podcastId": "pod_country_018", "title": "Behind the Curtains of the Grand Ole Opry", "duration": "01:05:00", "publishDate": new Date("2024-04-10"), "status": "PUBLISHED" },
    { "episodeId": "ep_064", "podcastId": "pod_country_018", "title": "Stage Fright: Confessions from Touring Artists", "duration": "00:50:00", "publishDate": new Date("2024-04-17"), "status": "PUBLISHED" },
    { "episodeId": "ep_065", "podcastId": "pod_country_018", "title": "Festival Life: CMA Fest Stories", "duration": "00:45:00", "publishDate": new Date("2024-04-24"), "status": "PUBLISHED" },
    { "episodeId": "ep_066", "podcastId": "pod_country_018", "title": "Soundcheck Secrets from Nashville Studios", "duration": "00:38:00", "publishDate": new Date("2024-05-01"), "status": "PUBLISHED" },
    { "episodeId": "ep_067", "podcastId": "pod_country_018", "title": "The Evolution of Live Country Performances", "duration": "00:55:00", "publishDate": new Date("2024-05-08"), "status": "PUBLISHED" }
]);