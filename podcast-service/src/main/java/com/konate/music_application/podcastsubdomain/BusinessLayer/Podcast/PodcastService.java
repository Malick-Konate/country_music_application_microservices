package com.konate.music_application.podcastsubdomain.BusinessLayer.Podcast;

import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeRequestModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeResponseModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast.PodcastRequestModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast.PodcastResponseModel;
import java.util.List;

public interface PodcastService {
    List<PodcastResponseModel> getAllPodcast();

    PodcastResponseModel getPodcastById(String podcastId);

    List<PodcastResponseModel> getPodcastByHosname(String hostname);

    PodcastResponseModel getPodcastByTitle(String title);

    PodcastResponseModel createPodcast(PodcastRequestModel requestModel);

    PodcastResponseModel updatePodcast(String podcastId, PodcastRequestModel requestModel);

    void deletePodcast(String podcastID);
}
