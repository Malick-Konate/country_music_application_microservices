package com.konate.music_application.apigateway.BusinessLayer.Podcast.Podcast;

import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Podcast.PodcastServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PodcastServiceImpl implements  PodcastService {
    private final PodcastServiceClient podcastServiceClient;

    public PodcastServiceImpl(PodcastServiceClient podcastServiceClient) {
        this.podcastServiceClient = podcastServiceClient;
    }
    @Override
    public List<PodcastResponseModel> getAllPodcast() {
        return podcastServiceClient.getAllPodcasts();
    }

    @Override
    public PodcastResponseModel getPodcastById(String podcastId) {
        return podcastServiceClient.getPodcastById(podcastId);
    }

    @Override
    public List<PodcastResponseModel> getPodcastByHosname(String hostname) {
        return podcastServiceClient.getPodcastByHostname(hostname);
    }

    @Override
    public PodcastResponseModel getPodcastByTitle(String title) {
        return null;
    }

    @Override
    public PodcastResponseModel createPodcast(PodcastRequestModel requestModel) {
        return podcastServiceClient.createPodcast(requestModel);
    }

    @Override
    public PodcastResponseModel updatePodcast(String podcastId, PodcastRequestModel requestModel) {
        return podcastServiceClient.updatePodcast(podcastId, requestModel);
    }

    @Override
    public void deletePodcast(String podcastID) {
        podcastServiceClient.deletePodcast(podcastID);
    }
}
