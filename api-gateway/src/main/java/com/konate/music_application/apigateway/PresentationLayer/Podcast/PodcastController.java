package com.konate.music_application.apigateway.PresentationLayer.Podcast;

import com.konate.music_application.apigateway.BusinessLayer.Podcast.Episode.EpisodeService;
import com.konate.music_application.apigateway.BusinessLayer.Podcast.Podcast.PodcastService;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/podcasts")
public class PodcastController {
    private final PodcastService podcastService;

    private final EpisodeService episodeService;

    public PodcastController(PodcastService podcastService, EpisodeService episodeService) {
        this.podcastService = podcastService;
        this.episodeService = episodeService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<PodcastResponseModel>> getAllPodcasts() {
        return new ResponseEntity<>(podcastService.getAllPodcast(), HttpStatus.OK);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<PodcastResponseModel> createPodcast(PodcastRequestModel requestModel) {
        return new ResponseEntity<>(podcastService.createPodcast(requestModel), HttpStatus.CREATED);
    }

    @GetMapping(
            value = "/{podcastId}",
            produces = "application/json"
    )
    public ResponseEntity<PodcastResponseModel> getPodcastById(@PathVariable String podcastId) {
        return new ResponseEntity<>(podcastService.getPodcastById(podcastId), HttpStatus.OK);
    }

    @PutMapping(
            value = "/{podcastId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<PodcastResponseModel> updatePodcast(@PathVariable String podcastId, PodcastRequestModel requestModel) {
        return new ResponseEntity<>(podcastService.updatePodcast(podcastId, requestModel), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/{username}",
            produces = "application/json"
    )
    public ResponseEntity<HttpStatus> deletePodcast(@PathVariable String username) {
        podcastService.deletePodcast(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /// /// episode /// ///
    @GetMapping(
            value = "/{podcastId}/episodes",
            produces = "application/json"
    )
    public ResponseEntity<List<EpisodeResponseModel>> getEpisodesByPodcastId(@PathVariable String podcastId) {
        return new ResponseEntity<>(episodeService.getEpisodesByPodcastId(podcastId), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{podcastId}/episodes/{episodeId}",
            produces = "application/json"
    )
    public ResponseEntity<EpisodeResponseModel> getEpisodeById(@PathVariable String podcastId, @PathVariable String episodeId) {
        return new ResponseEntity<>(episodeService.getEpisodeById(podcastId, episodeId), HttpStatus.OK);
    }

    @PutMapping(
            value = "/{podcastId}/episodes/{episodeId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<EpisodeResponseModel> updateEpisode(@PathVariable String podcastId, @PathVariable String episodeId, EpisodeRequestModel requestModel) {
        return new ResponseEntity<>(episodeService.updateEpisode(podcastId, episodeId, requestModel), HttpStatus.OK);
    }

    @PostMapping(
            value = "/{podcastId}/episodes",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<EpisodeResponseModel> createEpisode(@PathVariable String podcastId, EpisodeRequestModel requestModel) {
        return new ResponseEntity<>(episodeService.createEpisode(podcastId, requestModel), HttpStatus.CREATED);
    }

    @DeleteMapping(
            value = "/{podcastId}/episodes/{episodeId}",
            produces = "application/json"
    )
    public ResponseEntity<HttpStatus> deleteEpisode(@PathVariable String podcastId, @PathVariable String episodeId) {
        episodeService.deleteEpisode(episodeId, podcastId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
