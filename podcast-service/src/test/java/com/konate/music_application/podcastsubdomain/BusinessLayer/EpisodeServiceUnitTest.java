package com.konate.music_application.podcastsubdomain.BusinessLayer;

import com.konate.music_application.podcastsubdomain.BusinessLayer.Episode.EpisodeServiceImpl;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.Episode;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.EpisodeRepository;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.EpisodeStatus;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.Podcast;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastRepository;
import com.konate.music_application.podcastsubdomain.Exceptions.InconsistentPodcastException;
import com.konate.music_application.podcastsubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.podcastsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.podcastsubdomain.MappingLayer.Episode.EpisodeRequestMapper;
import com.konate.music_application.podcastsubdomain.MappingLayer.Episode.EpisodeResponseMapper;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpisodeServiceUnitTest {
    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private PodcastRepository podcastRepository;

    @Mock
    private EpisodeRequestMapper requestMapper;

    @Mock
    private EpisodeResponseMapper responseMapper;

    @InjectMocks
    private EpisodeServiceImpl episodeService;

    private final String VALID_PODCAST_ID = "pod_country_016";
    private final String VALID_EPISODE_ID = "ep_056";

    // --- NEGATIVE / EXCEPTION TESTS ---

    @Test
    void createEpisode_PodcastNotFound_ThrowsNotFoundException() {
        EpisodeRequestModel request = buildValidEpisodeRequest();
        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> episodeService.createEpisode(VALID_PODCAST_ID, request));
    }

    @Test
    void createEpisode_DuplicateTitle_ThrowsInvalidInputException() {
        EpisodeRequestModel request = buildValidEpisodeRequest();
        request.setEpisodeTitle("Duplicate Title");

        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(new Podcast());
        when(episodeRepository.findAllByEpisodeTitle("Duplicate Title")).thenReturn(new Episode());

        assertThrows(InvalidInputException.class, () -> episodeService.createEpisode(VALID_PODCAST_ID, request));
    }

    @Test
    void createEpisode_DurationZero_ThrowsInconsistentPodcastException() {
        EpisodeRequestModel request = buildValidEpisodeRequest();
        request.setDuration(new Time(0)); // 0 milliseconds

        assertThrows(InconsistentPodcastException.class, () -> episodeService.createEpisode(VALID_PODCAST_ID, request));
    }

    @Test
    void createEpisode_PublishedWithoutDate_ThrowsInconsistentPodcastException() {
        EpisodeRequestModel request = buildValidEpisodeRequest();
        request.setStatus(EpisodeStatus.PUBLISHED);
        request.setPublishDate(null); // Missing date

        assertThrows(InconsistentPodcastException.class, () -> episodeService.createEpisode(VALID_PODCAST_ID, request));
    }

    @Test
    void createEpisode_PublishedFutureDate_ThrowsInconsistentPodcastException() {
        EpisodeRequestModel request = buildValidEpisodeRequest();
        request.setStatus(EpisodeStatus.PUBLISHED);
        // Set date to 10 days in the future
        request.setPublishDate(new Date(System.currentTimeMillis() + 864000000L));

        assertThrows(InconsistentPodcastException.class, () -> episodeService.createEpisode(VALID_PODCAST_ID, request));
    }

    @Test
    void getEpisodeById_MismatchPodcastId_ThrowsNotFoundException() {
        Podcast podcast = new Podcast();
        podcast.setPodcastId("POD-A");

        Episode episode = new Episode();
        episode.setPodcastId("POD-B"); // Belongs to a different podcast

        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(podcast);
        when(episodeRepository.findAllByEpisodeId(VALID_EPISODE_ID)).thenReturn(episode);

        assertThrows(InvalidInputException.class, () -> episodeService.getEpisodeById(VALID_PODCAST_ID, VALID_EPISODE_ID));
    }

    @Test
    void deleteEpisode_EpisodeNotFound_ThrowsNotFoundException() {
        Podcast podcast = new Podcast();
        podcast.setPodcastId(VALID_PODCAST_ID);

        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(podcast);
        when(episodeRepository.findAllByEpisodeId(VALID_EPISODE_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> episodeService.deleteEpisode(VALID_EPISODE_ID, VALID_PODCAST_ID));
    }

    @Test
    void deleteEpisode_NullPodcastId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> episodeService.deleteEpisode(VALID_EPISODE_ID, null));
    }

    @Test
    void deleteEpisode_NullEpisodeId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> episodeService.deleteEpisode(null, VALID_PODCAST_ID));
    }

    @Test
    void deleteEpisode_EmptyEpisodeId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> episodeService.deleteEpisode("   ", VALID_PODCAST_ID));
    }

    @Test
    void deleteEpisode_NotIdentical_PodcastId_ThrowsInvalidInputException() {
        Podcast podcast = new Podcast();
        // It's best to set this to the valid constant so it matches the mock below
        podcast.setPodcastId(VALID_PODCAST_ID);

        Episode episode = new Episode();
        // Belongs to a different podcast, triggering the invariant
        episode.setPodcastId("POD-B");

        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(podcast);
        when(episodeRepository.findAllByEpisodeId(VALID_EPISODE_ID)).thenReturn(episode);

        // 1. Assert NotFoundException.class
        // 2. Ensure parameter order matches your service (episodeId, podcastId)
        assertThrows(InvalidInputException.class, () ->
                episodeService.deleteEpisode(VALID_EPISODE_ID, VALID_PODCAST_ID)
        );
    }

    @Test
    void getEpisode_NullPodcastId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> episodeService.getEpisodeById(null, VALID_PODCAST_ID));
    }

    @Test
    void getEpisode_NullEpisodeId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> episodeService.getEpisodeById(VALID_EPISODE_ID, null));
    }

    @Test
    void getEpisode_EmptyEpisodeId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> episodeService.getEpisodeById("   ", VALID_PODCAST_ID));
    }

    @Test
    void getEpisode_PodcastNotFound_ThrowsNotFoundException() {
        // Arrange: Only stub the call that actually gets executed before the exception is thrown
        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(null);

        // Act & Assert: Ensure the arguments are in the correct order (podcastId, episodeId)
        assertThrows(NotFoundException.class, () ->
                episodeService.getEpisodeById(VALID_PODCAST_ID, VALID_EPISODE_ID)
        );
    }

    @Test
    void updateEpisode_NullRequest_ThrowsInvalidInputException() {
        // Act & Assert
        // We remove the when(...) stub because the null check happens before the repository call.
        assertThrows(InvalidInputException.class, () ->
                episodeService.updateEpisode(VALID_PODCAST_ID, VALID_EPISODE_ID, null)
        );
    }

    @Test
    void updateEpisode_DuplicateTitle_ThrowsInvalidInputException() {
        // Arrange
        String duplicateTitle = "Existing Episode Title";

        // 1. Create the request with the title we want to change to
        EpisodeRequestModel request = buildValidEpisodeRequest();
        request.setEpisodeTitle(duplicateTitle);

        // 2. Mock the Podcast lookup
        Podcast podcast = new Podcast();
        podcast.setPodcastId(VALID_PODCAST_ID);
        when(podcastRepository.findAllByPodcastId(VALID_PODCAST_ID)).thenReturn(podcast);

        // 3. Mock the current Episode lookup (the one we are updating)
        Episode currentEpisode = new Episode();
        currentEpisode.setEpisodeId(VALID_EPISODE_ID);
        currentEpisode.setPodcastId(VALID_PODCAST_ID);
        when(episodeRepository.findAllByEpisodeId(VALID_EPISODE_ID)).thenReturn(currentEpisode);

        // 4. Mock the title check: Return a DIFFERENT episode when searching by that title
        Episode anotherEpisode = new Episode();
        anotherEpisode.setEpisodeId("different-episode-id"); // Different from VALID_EPISODE_ID
        anotherEpisode.setEpisodeTitle(duplicateTitle);

        when(episodeRepository.findAllByEpisodeTitle(duplicateTitle)).thenReturn(anotherEpisode);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                episodeService.updateEpisode(VALID_PODCAST_ID, VALID_EPISODE_ID, request)
        );

        assertEquals("Another episode with title '" + duplicateTitle + "' already exists.", exception.getMessage());

        // Verify the repository was actually checked
        verify(episodeRepository).findAllByEpisodeTitle(duplicateTitle);
    }
    // Helper method

    private EpisodeRequestModel buildValidEpisodeRequest() {
        EpisodeRequestModel request = new EpisodeRequestModel();
        request.setEpisodeTitle("Valid Title");
        request.setDuration(Time.valueOf("00:30:00"));
        request.setStatus(EpisodeStatus.PUBLISHED);
        request.setPublishDate(new Date(System.currentTimeMillis() - 10000)); // Past date
        return request;
    }

}
