package com.konate.music_application.podcastsubdomain.DataLayer.Episode;

//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EpisodeRepository extends MongoRepository<Episode, String>{
//    Episode findAllByEpisodeIdentifier_EpisodeId(String episodeId);
    Episode findAllByEpisodeId(String episodeId);

//    List<Episode> findAllByPodcastIdentifier_PodcastId(String podcastId);
    List<Episode> findAllByPodcastId(String podcastId);

    Episode findAllByEpisodeTitle(String title);

//    List<Episode> findAllByPodcastIdentifier_PodcastIdAndEpisodeIdentifier_EpisodeId(String podcastId, String episodeId);
//    List<Episode> findAllByPodcastIdAndEpisodeId(String podcastId, String episodeId);
}
