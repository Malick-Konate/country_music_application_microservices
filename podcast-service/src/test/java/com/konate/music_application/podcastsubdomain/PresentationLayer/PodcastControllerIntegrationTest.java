package com.konate.music_application.podcastsubdomain.PresentationLayer;


import com.konate.music_application.podcastsubdomain.DataLayer.Episode.EpisodeStatus;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastPricing;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeRequestModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeResponseModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast.PodcastRequestModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast.PodcastResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
//@Sql({"/schema.sql", "/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PodcastControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String BASE_URL = "/api/v1/podcasts";

    // Existing IDs from your data.sql
    private final String VALID_PODCAST_ID = "pod_country_017";
    private final String VALID_EPISODE_ID = "ep_058";
    private final String VALID_HOSTNAME = "Earl Scruggs Jr."; // Added period
    private final String NOT_FOUND_ID = "NOT-FOUND";

    @BeforeEach
    void setUp() {
        // If you need to reset any state before each test, do it here.
        // With @DirtiesContext, the context is refreshed after each test, so the database should be clean.
    }

    // --- PODCAST TESTS ---

    @Test
    void whenPodcastsExist_thenReturnAllPodcasts() {
        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(PodcastResponseModel.class)
                .value(podcasts -> {
                    assertNotNull(podcasts);
                    assertFalse(podcasts.isEmpty());
                });
    }

    @Test
    void whenValidPodcastId_thenReturnPodcast() {
        webTestClient.get()
                .uri(BASE_URL + "/" + VALID_PODCAST_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PodcastResponseModel.class)
                .value(podcast -> {
                    assertEquals(VALID_PODCAST_ID, podcast.getPodcastId());
                    assertEquals("Country Song Breakdown", podcast.getTitle());
                    assertEquals("Studio Session Sam", podcast.getHostname());
                    assertNotEquals(VALID_HOSTNAME, podcast.getHostname());
                });
    }

    @Test
    void whenValidHostname_thenReturnPodcasts() {
        webTestClient.get()
                .uri(BASE_URL + "/by-host/" + VALID_HOSTNAME)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PodcastResponseModel.class)
                .value(podcasts -> {
                    assertFalse(podcasts.isEmpty());
                    assertEquals(VALID_HOSTNAME, podcasts.get(0).getHostname());
                });
    }

    @Test
    void whenCreateValidPodcast_thenStatusCreated() {
        PodcastRequestModel request = new PodcastRequestModel("New Podcast", "HostX", "Desc", PodcastPricing.FREE);

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PodcastResponseModel.class)
                .value(response -> assertEquals("New Podcast", response.getTitle()));
    }

    @Test
    void whenUpdatePodcast_thenReturnUpdatedPodcast() {
        PodcastRequestModel updateRequest = new PodcastRequestModel("Updated Title", "NewHost", "New Desc", PodcastPricing.SUBSCRIPTION);

        webTestClient.put()
                .uri(BASE_URL + "/" + VALID_PODCAST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PodcastResponseModel.class)
                .value(response -> assertEquals("Updated Title", response.getTitle()));
    }

    // --- EPISODE TESTS ---

    @Test
    void whenValidPodcastAndEpisode_thenReturnEpisode() {
        // Matches structure: /api/v1/podcasts/{podcastId}/episodes/{episodeId}
        webTestClient.get()
                .uri(BASE_URL + "/" + "pod_country_017" + "/episodes/" + VALID_EPISODE_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EpisodeResponseModel.class)
                .value(episode -> assertEquals(VALID_EPISODE_ID, episode.getEpisodeId()));
    }

    @Test
    void whenCreateEpisode_thenReturnCreatedEpisode() {
        EpisodeRequestModel request = new EpisodeRequestModel("New Episode", Time.valueOf("00:30:00"), new Date(), EpisodeStatus.PUBLISHED);

        webTestClient.post()
                .uri(BASE_URL + "/" + VALID_PODCAST_ID + "/episodes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EpisodeResponseModel.class)
                .value(response -> assertEquals("New Episode", response.getEpisodeTitle()));
    }

    @Test
    void whenDeleteEpisode_thenReturnNoContent() {
        // Note: Check your controller param order. You have @PathVariable episodeId, then podcastId in deleteEpisode
        webTestClient.delete()
                .uri(BASE_URL + "/pod_country_017/episodes/" + VALID_EPISODE_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    // --- NEGATIVE TESTS ---

    @Test
    void whenPodcastIdNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenEpisodeIdNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/" + VALID_PODCAST_ID + "/episodes/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}