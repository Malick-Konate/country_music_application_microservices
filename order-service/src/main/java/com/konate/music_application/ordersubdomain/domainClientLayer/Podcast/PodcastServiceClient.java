package com.konate.music_application.ordersubdomain.domainClientLayer.Podcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.ordersubdomain.Exceptions.HttpErrorInfo;
import com.konate.music_application.ordersubdomain.Exceptions.InconsistentPodcastException;
import com.konate.music_application.ordersubdomain.Exceptions.NotFoundException;
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

    public List<PodcastModel> getAllPodcasts() {
        try {

            String url = Podcast_SERVICE_BASE_url;
            log.info("Calling Podcast Service at {}", url);

            PodcastModel[] podcasts = restTemplate.getForObject(url, PodcastModel[].class);
            return Arrays.asList(podcasts);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public PodcastModel getPodcastById(String podcastId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.getForObject(url, PodcastModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public PodcastModel createPodcast(PodcastModel podcast) {
        try {
            String url = Podcast_SERVICE_BASE_url;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.postForObject(url, podcast, PodcastModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public PodcastModel updatePodcast(String podcastId, PodcastModel podcast) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId;
            log.info("Calling Podcast Service at {}", url);
            restTemplate.put(url, podcast, PodcastModel.class);
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

    public List<PodcastModel> getPodcastByHostname(String hostname){
        try{
            String url = Podcast_SERVICE_BASE_url + "/by-host/" + hostname;
            log.info("Calling Podcast Service at {}", url);
            return Arrays.asList(restTemplate.getForObject(url, PodcastModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public PodcastModel getPodcastByTitle(String title){
        try{
            String url = Podcast_SERVICE_BASE_url + "/podcast-title/" + title;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.getForObject(url, PodcastModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    /// /// Episodes /// ///
    public EpisodeModel createEpisode(String podcastId, EpisodeModel episode) {
        try{
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes";
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.postForObject(url, episode, EpisodeModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public EpisodeModel updateEpisode(String podcastId, String episodeId, EpisodeModel episode) {
        try{
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes/" + episodeId;
            log.info("Calling Podcast Service at {}", url);
            restTemplate.put(url, episode, EpisodeModel.class);
            return getEpisode(podcastId, episodeId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteEpisode(String podcastId, String episodeId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes/" + episodeId;
            log.info("Calling Podcast Service at {}", url);
             restTemplate.delete(url, EpisodeModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public EpisodeModel getEpisode(String podcastId, String episodeId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes/" + episodeId;
            log.info("Calling Podcast Service at {}", url);
            return restTemplate.getForObject(url, EpisodeModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<EpisodeModel> getEpisodes(String podcastId) {
        try {
            String url = Podcast_SERVICE_BASE_url + "/" + podcastId + "/episodes";
            log.info("Calling Podcast Service at {}", url);
            return Arrays.asList(restTemplate.getForObject(url, EpisodeModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InconsistentPodcastException(getErrorMessage(ex));
        }

        log.warn("Got an unexpected HTTP error from Podcast Service: {}, will rethrow it", ex.getStatusCode());
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
