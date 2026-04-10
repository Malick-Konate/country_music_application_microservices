package com.konate.music_application.catalogsubdomain.Business;

import com.konate.music_application.catalogsubdomain.BusinessLayer.AlbumServiceImpl;
import com.konate.music_application.catalogsubdomain.DataLayer.*;
import com.konate.music_application.catalogsubdomain.Exceptions.InconsistentAlbumException;
import com.konate.music_application.catalogsubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.catalogsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.catalogsubdomain.PresentationLayer.AlbumRequestModel;
import com.konate.music_application.catalogsubdomain.PresentationLayer.AlbumResponseModel;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistModel;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistServiceClient;
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
class AlbumServiceUnitTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistServiceClient artistServiceClient;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void createAlbum_ArtistNotFound_ThrowsNotFoundException() {
        // Arrange
        AlbumRequestModel request = AlbumRequestModel.builder().artistId("NON-EXISTENT").build();
        when(artistServiceClient.getArtistById("NON-EXISTENT")).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> albumService.createAlbum(request));
    }

    @Test
    void createAlbum_NoSongs_ThrowsInvalidInputException() {
        // Arrange
        AlbumRequestModel request = AlbumRequestModel.builder()
                .artistId("ART-001")
                .song(new ArrayList<>()) // Empty list
                .build();

        when(artistServiceClient.getArtistById("ART-001")).thenReturn(new ArtistModel());

        // Act & Assert
        assertThrows(InconsistentAlbumException.class, () -> albumService.createAlbum(request));
    }

    @Test
    void getAlbumByTitle_NotFound_ThrowsNotFoundException() {
        // Arrange
        when(albumRepository.findAllByTitle("Ghost")).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> albumService.getAlbumByTitle("Ghost"));
    }

    @Test
    void updateAlbum_AlbumIdNotFound_ThrowsNotFoundException() {
        // Arrange
        when(albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-XXX")).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> albumService.updateAlbum("ALB-XXX", new AlbumRequestModel()));
    }
}