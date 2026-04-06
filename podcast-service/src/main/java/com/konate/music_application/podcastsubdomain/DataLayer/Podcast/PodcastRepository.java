package com.konate.music_application.podcastsubdomain.DataLayer.Podcast;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PodcastRepository extends MongoRepository<Podcast, String>{
    List<Podcast> findAllByHostname(String hostname);
    
    Podcast findAllByTitle(String title);

//    Podcast findAllByPodcastIdentifier_PodcastId(String podcastIdentifierPodcastId);

    Podcast findAllByPodcastId(String podcastId);
}
