package com.konate.music_application.apigateway.domainClientLayer.Podcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.apigateway.Exceptions.HttpErrorInfo;
import com.konate.music_application.apigateway.Exceptions.NotFoundException;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
@Slf4j
public class PodcastServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String Podcast_SERVICE_BASE_url;

    public PodcastServiceClient(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.podcast.host}") String podcatServiceHost,
                                @Value("${app.podcast.port}") String podcastServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
        this.Podcast_SERVICE_BASE_url = "http://" + podcatServiceHost + ":" + podcastServicePort + "/api/v1/podcasts";
    }

    public List<PodcastResponseModel> getAllPodcasts() {
        try {

            String url = Podcast_SERVICE_BASE_url;
            log.info("Calling Podcast Service at {}", url);

            PodcastResponseModel[] podcasts = restTemplate.getForObject(url, PodcastResponseModel[].class);
            return Arrays.asList(podcasts);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public PodcastResponseModel getPodcastById(String podcastId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.getForObject(url, PodcastResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public PodcastResponseModel createPodcast(PodcastRequestModel podcast) {
        try {
            String url = Podcast_SERVICE_BASE_url;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.postForObject(url, podcast, PodcastResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public PodcastResponseModel updatePodcast(String podcastId, PodcastRequestModel podcast) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId;
            log.info("Calling Podcast Service at {}", url);
            restTemplate.put(url, podcast, EpisodeRequestModel.class);
            return getPodcastById(podcastId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public  void deletePodcast(String podcastId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId;
            log.info("Calling Podcast Service at {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<PodcastResponseModel> getPodcastByHostname(String hostname){
        try{
            String url = Podcast_SERVICE_BASE_url + "/by-host/" + hostname;
            log.info("Calling Podcast Service at {}", url);
            return Arrays.asList(restTemplate.getForObject(url, PodcastResponseModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    /// /// Episodes /// ///
    public EpisodeResponseModel createEpisode(String podcastId, EpisodeRequestModel episode) {
        try{
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes";
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.postForObject(url, episode, EpisodeResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public EpisodeResponseModel updateEpisode(String podcastId, String episodeId, EpisodeRequestModel episode) {
        try{
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes/" + episodeId;
            log.info("Calling Podcast Service at {}", url);
            restTemplate.put(url, episode, EpisodeResponseModel.class);
            return restTemplate.patchForObject(url, episode, EpisodeResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteEpisode(String podcastId, String episodeId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes/" + episodeId;
            log.info("Calling Podcast Service at {}", url);
             restTemplate.delete(url, EpisodeResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public EpisodeResponseModel getEpisode(String podcastId, String episodeId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes/" + episodeId;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.getForObject(url, EpisodeResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<EpisodeResponseModel> getEpisodes(String podcastId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes";
            log.info("Calling Podcast Service at {}", url);
            return Arrays.asList(restTemplate.getForObject(url, EpisodeResponseModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new NotFoundException(getErrorMessage(ex));
        }

        log.warn("Got an unexpected HTTP error from Artist Service: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }


    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            // Assuming the error response body can be mapped to HttpErrorInfo
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            // Fallback if the error body cannot be parsed
            return ioex.getMessage();
        }
    }


}
