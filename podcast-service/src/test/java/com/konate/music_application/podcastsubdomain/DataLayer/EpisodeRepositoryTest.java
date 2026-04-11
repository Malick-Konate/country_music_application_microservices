package com.konate.music_application.podcastsubdomain.DataLayer;


import com.konate.music_application.podcastsubdomain.DataLayer.Episode.Episode;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.EpisodeRepository;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.EpisodeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest(properties = {
        "de.flapdoodle.mongodb.embedded.version=7.0.5"
})
public class EpisodeRepositoryTest {

    @Autowired
    private EpisodeRepository episodeRepository;

    @BeforeEach
    public void setUp() {
        episodeRepository.deleteAll(); // Clean the embedded Mongo collection before each test
    }

    @Test
    public void whenEpisodesExist_thenReturnAllEpisodes() {
        // Arrange
        Episode ep1 = buildSampleEpisode("EP-001", "POD-123", "Ep 1");
        Episode ep2 = buildSampleEpisode("EP-002", "POD-123", "Ep 2");

        episodeRepository.save(ep1);
        episodeRepository.save(ep2);

        // Act
        List<Episode> episodes = episodeRepository.findAll();

        // Assert
        assertNotNull(episodes);
        assertEquals(2, episodes.size());
    }

    @Test
    public void whenEpisodeExists_thenReturnByEpisodeId() {
        // Arrange
        Episode episode = buildSampleEpisode("EP-999", "POD-777", "Special Episode");
        episodeRepository.save(episode);

        // Act
        Episode found = episodeRepository.findAllByEpisodeId("EP-999");

        // Assert
        assertNotNull(found);
        assertEquals("EP-999", found.getEpisodeId());
        assertEquals("Special Episode", found.getEpisodeTitle());
    }

    @Test
    public void whenEpisodesExist_thenReturnByPodcastId() {
        // Arrange
        Episode ep1 = buildSampleEpisode("EP-003", "POD-TARGET", "Target Ep 1");
        Episode ep2 = buildSampleEpisode("EP-004", "POD-TARGET", "Target Ep 2");
        Episode ep3 = buildSampleEpisode("EP-005", "POD-OTHER", "Other Ep");

        episodeRepository.save(ep1);
        episodeRepository.save(ep2);
        episodeRepository.save(ep3);

        // Act
        List<Episode> found = episodeRepository.findAllByPodcastId("POD-TARGET");

        // Assert
        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(e -> e.getPodcastId().equals("POD-TARGET")));
    }

    @Test
    public void whenEpisodeExists_thenReturnByTitle() {
        // Arrange
        Episode episode = buildSampleEpisode("EP-111", "POD-111", "Unique Title Search");
        episodeRepository.save(episode);

        // Act
        Episode found = episodeRepository.findAllByEpisodeTitle("Unique Title Search");

        // Assert
        assertNotNull(found);
        assertEquals("EP-111", found.getEpisodeId());
    }

    @Test
    public void whenEpisodeDoesNotExist_thenReturnNullOrEmpty() {
        // Act
        Episode foundById = episodeRepository.findAllByEpisodeId("NON-EXISTENT");
        List<Episode> foundByPodcast = episodeRepository.findAllByPodcastId("NON-EXISTENT-POD");

        // Assert
        assertNull(foundById);
        assertTrue(foundByPodcast.isEmpty());
    }

    @Test
    public void whenDeleteEpisode_thenNotFound() {
        // Arrange
        Episode episode = buildSampleEpisode("EP-000", "POD-000", "To Be Deleted");
        episodeRepository.save(episode);

        // Act
        episodeRepository.delete(episode);
        Episode found = episodeRepository.findAllByEpisodeId("EP-000");

        // Assert
        assertNull(found);
    }

    private Episode buildSampleEpisode(String episodeId, String podcastId, String title) {
        Episode episode = new Episode();
        episode.setEpisodeId(episodeId); // Setting the String field directly for Mongo
        episode.setPodcastId(podcastId); // Setting the String field directly for Mongo
        episode.setEpisodeTitle(title);
        // Note: duration is mapped as a String in your Document
        episode.setDuration("00:30:00");
        episode.setPublishDate(new Date());
        episode.setStatus(EpisodeStatus.PUBLISHED);
        return episode;
    }
}