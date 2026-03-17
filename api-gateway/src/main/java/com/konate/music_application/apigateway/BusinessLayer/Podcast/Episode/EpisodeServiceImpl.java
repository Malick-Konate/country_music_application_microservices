package com.konate.music_application.apigateway.BusinessLayer.Podcast.Episode;

import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Podcast.PodcastServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeServiceImpl implements  EpisodeService {
    private final PodcastServiceClient podcastServiceClient;

    public EpisodeServiceImpl(PodcastServiceClient podcastServiceClient) {
        this.podcastServiceClient = podcastServiceClient;
    }

    @Override
    public List<EpisodeResponseModel> getEpisodesByPodcastId(String podcastId) {
        return podcastServiceClient.getEpisodes(podcastId);
    }

    @Override
    public EpisodeResponseModel createEpisode(String podcastId, EpisodeRequestModel requestModel) {
        return podcastServiceClient.createEpisode(podcastId, requestModel);
    }

    @Override
    public EpisodeResponseModel getEpisodeById(String podcastId, String episodeId) {
        return podcastServiceClient.getEpisode(podcastId, episodeId);
    }

    @Override
    public EpisodeResponseModel updateEpisode(String podcastId, String episodeId, EpisodeRequestModel requestModel) {
        return podcastServiceClient.updateEpisode(podcastId, episodeId, requestModel);
    }

    @Override
    public void deleteEpisode(String episodeId, String podcastId) {
        podcastServiceClient.deleteEpisode(episodeId, podcastId);
    }
}
