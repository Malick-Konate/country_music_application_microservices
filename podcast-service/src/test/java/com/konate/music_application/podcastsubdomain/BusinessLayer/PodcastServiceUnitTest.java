package com.konate.music_application.podcastsubdomain.BusinessLayer;
import com.konate.music_application.podcastsubdomain.BusinessLayer.Podcast.PodcastServiceImpl;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.Podcast;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastRepository;
import com.konate.music_application.podcastsubdomain.Exceptions.InconsistentPodcastException;
import com.konate.music_application.podcastsubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.podcastsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.podcastsubdomain.MappingLayer.Podcast.PodcastRequestMapper;
import com.konate.music_application.podcastsubdomain.MappingLayer.Podcast.PodcastResponseMapper;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast.PodcastRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PodcastServiceUnitTest {
    @Mock
    private PodcastRepository podcastRepository;

    @Mock
    private PodcastRequestMapper requestMapper;

    @Mock
    private PodcastResponseMapper responseMapper;

    @InjectMocks
    private PodcastServiceImpl podcastService;

    // --- POSITIVE TESTS ---

    @Test
    void whenGetAllPodcasts_thenRepositoryIsCalled() {
        podcastService.getAllPodcast();
        verify(podcastRepository, times(1)).findAll();
    }

    // --- NEGATIVE / EXCEPTION TESTS ---

    @Test
    void getPodcastById_NullId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.getPodcastById(null));
        assertThrows(InvalidInputException.class, () -> podcastService.getPodcastById("   "));
    }

    @Test
    void getPodcastById_NotFound_ThrowsNotFoundException() {
        when(podcastRepository.findAllByPodcastId("POD-999")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> podcastService.getPodcastById("POD-999"));
    }

    @Test
    void createPodcast_NullRequest_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> podcastService.createPodcast(null));
        assertEquals("the request cannot be empty or null", exception.getMessage());
    }

    @Test
    void createPodcast_EmptyTitle_ThrowsInconsistentPodcastException() {
        PodcastRequestModel request = new PodcastRequestModel();
        request.setTitle("   ");
        request.setHostname("ValidHost");

        assertThrows(InconsistentPodcastException.class, () -> podcastService.createPodcast(request));
    }

    @Test
    void createPodcast_HostnameWithSpaces_ThrowsInconsistentPodcastException() {
        PodcastRequestModel request = new PodcastRequestModel();
        request.setTitle("Valid Title");
        request.setHostname("Host Name With Spaces");

        assertThrows(InconsistentPodcastException.class, () -> podcastService.createPodcast(request));
    }

    @Test
    void updatePodcast_NullId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.updatePodcast(null, new PodcastRequestModel()));
    }

    @Test
    void updatePodcast_NullRequest_ThrowsNotFoundException() {
        // Based on your specific logic in PodcastServiceImpl
        when(podcastRepository.findAllByPodcastId("POD-123")).thenReturn(new Podcast());
        assertThrows(NotFoundException.class, () -> podcastService.updatePodcast("POD-123", null));
    }

    @Test
    void deletePodcast_NotFound_ThrowsNotFoundException() {
        when(podcastRepository.findAllByPodcastId("POD-999")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> podcastService.deletePodcast("POD-999"));
    }

    @Test
    void deletePodcast_NullId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.deletePodcast(null));
    }

    @Test
    void deletePodcast_EmptyId_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.deletePodcast("   "));
    }

    @Test
    void getPodcastByTitle_NullTitle_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.getPodcastByTitle(null));
    }

    @Test
    void getPodcastByTitle_EmptyTitle_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.getPodcastByTitle("   "));
    }

    @Test
    void getPodcastByTitle_NotFound_ThrowsNotFoundException() {
        when(podcastRepository.findAllByTitle("Valid Title")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> podcastService.getPodcastByTitle("Valid Title"));
    }

    @Test
    void getPodcastByHostname_NullHostname_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.getPodcastByHosname(null));
    }

    @Test
    void getPodcastByHostname_EmptyHostname_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> podcastService.getPodcastByHosname("   "));
    }

    @Test
    void getPodcastByHostname_NotFound_ThrowsNotFoundException() {
        when(podcastRepository.findAllByHostname("Valid Hostname")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> podcastService.getPodcastByHosname("Valid Hostname"));
    }
}

