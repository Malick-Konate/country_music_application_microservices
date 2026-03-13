package com.konate.music_application.podcastsubdomain.DataLayer.Episode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode, Integer>{
    Episode findAllByEpisodeIdentifier_EpisodeId(String episodeId);

    List<Episode> findAllByPodcastIdentifier_PodcastId(String podcastId);

    Episode findAllByEpisodeTitle(String title);

    List<Episode> findAllByPodcastIdentifier_PodcastIdAndEpisodeIdentifier_EpisodeId(String podcastId, String episodeId);
}
