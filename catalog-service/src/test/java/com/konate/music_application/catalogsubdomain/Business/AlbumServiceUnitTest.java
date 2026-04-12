package com.konate.music_application.catalogsubdomain.Business;

import com.konate.music_application.catalogsubdomain.BusinessLayer.AlbumServiceImpl;
import com.konate.music_application.catalogsubdomain.DataLayer.*;
import com.konate.music_application.catalogsubdomain.Exceptions.InconsistentAlbumException;
import com.konate.music_application.catalogsubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.catalogsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.catalogsubdomain.MappingLayer.AlbumRequestMapper;
import com.konate.music_application.catalogsubdomain.MappingLayer.AlbumResponseMapper;
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
public class AlbumServiceUnitTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistServiceClient artistServiceClient;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Mock
    private AlbumRequestMapper albumRequestMapper;

    @Mock
    private AlbumResponseMapper albumResponseMapper;

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

    @Test
    void createAlbum_ValidInput_ReturnsAlbum() {
        // Arrange
        AlbumRequestModel request = AlbumRequestModel.builder()
                .artistId("ART-001")
                .song(List.of(new Song()))
                .build();

        ArtistModel artist = new ArtistModel();
        artist.setFirstName("John");
        artist.setLastName("Doe");

        Album albumEntity = new Album();
        Album savedAlbum = new Album();

        when(artistServiceClient.getArtistById("ART-001")).thenReturn(artist);
        when(albumRequestMapper.toAlbum(any(), any(), any())).thenReturn(albumEntity);
        when(albumRepository.save(albumEntity)).thenReturn(savedAlbum);
        when(albumResponseMapper.toResponseModel(savedAlbum)).thenReturn(new AlbumResponseModel());

        // Act
        AlbumResponseModel result = albumService.createAlbum(request);

        // Assert
        assertNotNull(result);
        verify(albumRepository, times(1)).save(albumEntity);
    }

    @Test
    void getAlbumById_ValidId_ReturnsAlbum() {
        // Arrange
        Album album = new Album();
        album.setArtist_ID("ART-001");

        ArtistModel artist = new ArtistModel();
        artist.setFirstName("John");
        artist.setLastName("Doe");

        when(albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-1")).thenReturn(album);
        when(artistServiceClient.getArtistById("ART-001")).thenReturn(artist);
        when(albumResponseMapper.toResponseModel(album)).thenReturn(new AlbumResponseModel());

        // Act
        AlbumResponseModel result = albumService.getAlbumById("ALB-1");

        // Assert
        assertNotNull(result);
    }

    @Test
    void getAllAlbums_ReturnsList() {
        // Arrange
        Album album = new Album();
        album.setArtist_ID("ART-001");

        ArtistModel artist = new ArtistModel();
        artist.setFirstName("John");
        artist.setLastName("Doe");

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(artistServiceClient.getArtistById("ART-001")).thenReturn(artist);
        when(albumResponseMapper.toResponseModel(album)).thenReturn(new AlbumResponseModel());

        // Act
        List<AlbumResponseModel> result = albumService.getAllAlbums();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void updateAlbum_ValidInput_ReturnsUpdatedAlbum() {
        // Arrange
        Album existing = new Album();
        existing.setArtist_ID("ART-001");

        AlbumRequestModel request = AlbumRequestModel.builder()
                .artistId("ART-001")
                .title("New Title")
                .build();

        ArtistModel artist = new ArtistModel();
        artist.setFirstName("John");
        artist.setLastName("Doe");

        when(albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-1")).thenReturn(existing);
        when(artistServiceClient.getArtistById("ART-001")).thenReturn(artist);
        when(albumRepository.save(existing)).thenReturn(existing);
        when(albumResponseMapper.toResponseModel(existing)).thenReturn(new AlbumResponseModel());

        // Act
        AlbumResponseModel result = albumService.updateAlbum("ALB-1", request);

        // Assert
        assertNotNull(result);
        verify(albumRepository).save(existing);
    }

    @Test
    void deleteAlbum_ValidId_DeletesAlbum() {
        // Arrange
        Album album = new Album();
        when(albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-1")).thenReturn(album);

        // Act
        albumService.deleteAlbum("ALB-1");

        // Assert
        verify(albumRepository, times(1)).delete(album);
    }

    @Test
    void getAlbumByTitle_ValidTitle_ReturnsAlbum() {
        // Arrange
        Album album = new Album();
        album.setArtist_ID("ART-001");

        ArtistModel artist = new ArtistModel();
        artist.setFirstName("John");
        artist.setLastName("Doe");

        when(albumRepository.findAllByTitle("Test")).thenReturn(album);
        when(artistServiceClient.getArtistById("ART-001")).thenReturn(artist);
        when(albumResponseMapper.toResponseModel(album)).thenReturn(new AlbumResponseModel());

        // Act
        AlbumResponseModel result = albumService.getAlbumByTitle("Test");

        // Assert
        assertNotNull(result);
    }
}