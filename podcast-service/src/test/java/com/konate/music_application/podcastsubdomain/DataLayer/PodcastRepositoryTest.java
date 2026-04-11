package com.konate.music_application.podcastsubdomain.DataLayer;


import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.Podcast;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastPricing;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest(properties = {
        "de.flapdoodle.mongodb.embedded.version=7.0.5"
})
public class PodcastRepositoryTest {

    @Autowired
    private PodcastRepository podcastRepository;

    @BeforeEach
    public void setUp() {
        podcastRepository.deleteAll(); // Clean the embedded Mongo collection before each test
    }

    @Test
    public void whenPodcastsExist_thenReturnAllPodcasts() {
        // Arrange
        Podcast podcast1 = buildSamplePodcast("POD-001", "Honky Tonk History", "Clint");
        Podcast podcast2 = buildSamplePodcast("POD-002", "Bluegrass Breakdown", "Earl");

        podcastRepository.save(podcast1);
        podcastRepository.save(podcast2);

        // Act
        List<Podcast> podcasts = podcastRepository.findAll();

        // Assert
        assertNotNull(podcasts);
        assertEquals(2, podcasts.size());
    }

    @Test
    public void whenPodcastExists_thenReturnByPodcastId() {
        // Arrange
        Podcast podcast = buildSamplePodcast("POD-999", "Texas Red Dirt", "Pat");
        podcastRepository.save(podcast);

        // Act
        Podcast found = podcastRepository.findAllByPodcastId("POD-999");

        // Assert
        assertNotNull(found);
        assertEquals("POD-999", found.getPodcastId());
        assertEquals("Texas Red Dirt", found.getTitle());
    }

    @Test
    public void whenPodcastExists_thenReturnByHostname() {
        // Arrange
        Podcast podcast1 = buildSamplePodcast("POD-003", "Title 1", "SharedHost");
        Podcast podcast2 = buildSamplePodcast("POD-004", "Title 2", "SharedHost");
        podcastRepository.save(podcast1);
        podcastRepository.save(podcast2);

        // Act
        List<Podcast> found = podcastRepository.findAllByHostname("SharedHost");

        // Assert
        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals("SharedHost", found.get(0).getHostname());
    }

    @Test
    public void whenPodcastExists_thenReturnByTitle() {
        // Arrange
        Podcast podcast = buildSamplePodcast("POD-555", "Unique Title", "Host");
        podcastRepository.save(podcast);

        // Act
        Podcast found = podcastRepository.findAllByTitle("Unique Title");

        // Assert
        assertNotNull(found);
        assertEquals("Unique Title", found.getTitle());
    }

    @Test
    public void whenPodcastDoesNotExist_thenReturnNullOrEmpty() {
        // Act
        Podcast foundById = podcastRepository.findAllByPodcastId("NON-EXISTENT");
        List<Podcast> foundByHost = podcastRepository.findAllByHostname("Ghost Host");

        // Assert
        assertNull(foundById);
        assertTrue(foundByHost.isEmpty());
    }

    @Test
    public void whenUpdatePodcast_thenAttributesAreChanged() {
        // Arrange
        Podcast podcast = buildSamplePodcast("POD-888", "Old Title", "Host");
        podcastRepository.save(podcast);

        // Act
        Podcast existing = podcastRepository.findAllByPodcastId("POD-888");
        existing.setTitle("New Title");
        existing.setPricingModel(PodcastPricing.SUBSCRIPTION);
        podcastRepository.save(existing);

        Podcast updated = podcastRepository.findAllByPodcastId("POD-888");

        // Assert
        assertNotNull(updated);
        assertEquals("New Title", updated.getTitle());
        assertEquals(PodcastPricing.SUBSCRIPTION, updated.getPricingModel());
    }

    @Test
    public void whenDeletePodcast_thenNotFound() {
        // Arrange
        Podcast podcast = buildSamplePodcast("POD-000", "To Be Deleted", "Host");
        podcastRepository.save(podcast);

        // Act
        podcastRepository.delete(podcast);
        Podcast found = podcastRepository.findAllByPodcastId("POD-000");

        // Assert
        assertNull(found);
    }

    private Podcast buildSamplePodcast(String podcastId, String title, String hostname) {
        Podcast podcast = new Podcast();
        podcast.setPodcastId(podcastId); // Setting the String field directly for Mongo
        podcast.setTitle(title);
        podcast.setHostname(hostname);
        podcast.setDescription("Sample Description");
        podcast.setPricingModel(PodcastPricing.FREE);
        return podcast;
    }
}