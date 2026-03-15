package com.konate.music_application.apigateway.domainClientLayer.Podcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PodcastServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String Podcast_SERVICE_BASE_url;

    public PodcastServiceClient(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.podcast.host}") String podcatServiceHost,
                                @Value("${app.podcast.port}") String podcastServicePort){
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
        this.Podcast_SERVICE_BASE_url = "http://" + podcatServiceHost + ":" + podcastServicePort + "/api/v1/podcasts";
    }

}
