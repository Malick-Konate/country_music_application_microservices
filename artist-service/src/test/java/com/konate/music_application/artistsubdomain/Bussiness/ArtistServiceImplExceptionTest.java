package com.konate.music_application.artistsubdomain.Bussiness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.konate.music_application.artistsubdomain.BusinessLayer.ArtistServiceImpl;
import com.konate.music_application.artistsubdomain.DataLayer.*;
import com.konate.music_application.artistsubdomain.Exceptions.ArtistFound;
import com.konate.music_application.artistsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.artistsubdomain.MappingLayer.ArtistRequestMapper;
import com.konate.music_application.artistsubdomain.MappingLayer.ArtistResponseMapper;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistRequestModel;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class ArtistServiceImplExceptionTest {
    private ArtistRepository artistRepository;
    private ArtistRequestMapper requestMapper;
    private ArtistResponseMapper responseMapper;
    private ArtistServiceImpl service;

    @BeforeEach
    void setUp(){
        artistRepository = mock(ArtistRepository.class);
        requestMapper = mock(ArtistRequestMapper.class);
        responseMapper = mock(ArtistResponseMapper.class);
        service = new ArtistServiceImpl(artistRepository, requestMapper, responseMapper);
    }

    // --- POSITIVE TESTS ---

    @Test
    void whenArtistsExist_getAllArtists_shouldReturnList() {
        // Arrange
        Artist artist = new Artist();
        List<Artist> artists = List.of(artist);
        ArtistResponseModel responseModel = new ArtistResponseModel();

        when(artistRepository.findAll()).thenReturn(artists);
        when(responseMapper.entityListToRespondModel(artists)).thenReturn(List.of(responseModel));

        // Act
        List<ArtistResponseModel> result = service.getAllArtists();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository, times(1)).findAll();
    }

    @Test
    void whenIdExists_getArtistById_shouldReturnArtist() {
        // Arrange
        String artistId = "ART-123";
        Artist artist = new Artist();
        ArtistResponseModel responseModel = new ArtistResponseModel();

        when(artistRepository.findAllByArtistIdentifier_ArtistId(artistId)).thenReturn(artist);
        when(responseMapper.toRespondModel(artist)).thenReturn(responseModel);

        // Act
        ArtistResponseModel result = service.getArtistById(artistId);

        // Assert
        assertNotNull(result);
        verify(artistRepository, times(1)).findAllByArtistIdentifier_ArtistId(artistId);
    }

//    @Test
//    void whenValidRequest_createArtist_shouldReturnSavedArtist() {
//        // Arrange
//        ArtistRequestModel request = new ArtistRequestModel("Waylon", "Jennings", "Bio", Collections.emptyList(), Collections.emptyList());
//        Artist artistEntity = new Artist();
//        ArtistResponseModel responseModel = new ArtistResponseModel();
//
//        when(artistRepository.findAllByLastName("Jennings")).thenReturn(null);
//        when(requestMapper.toEntity(any(), any(), any())).thenReturn(artistEntity);
//        when(artistRepository.save(artistEntity)).thenReturn(artistEntity);
//        when(responseMapper.toRespondModel(artistEntity)).thenReturn(responseModel);
//
//        // Act
//        ArtistResponseModel result = service.createArtist(request);
//
//        // Assert
//        assertNotNull(result);
//        verify(artistRepository, times(1)).save(any(Artist.class));
//    }

    // --- NEGATIVE / EXCEPTION TESTS ---

    @Test
    void whenRequestModelIsNull_createArtist_thenThrowIllegalArgumentException(){
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.createArtist(null));
        assertEquals("Invalid input: Artist request model cannot be null", exception.getMessage());
    }

    @Test
    void whenLastNameAlreadyExists_createArtist_thenThrowArtistFoundException() {
        // Arrange
        ArtistRequestModel request = new ArtistRequestModel();
        request.setLastName("Jennings");
        Artist existingArtist = new Artist();
        existingArtist.setFirstName("Waylon");
        existingArtist.setLastName("Jennings");

        when(artistRepository.findAllByLastName("Jennings")).thenReturn(existingArtist);

        // Act & Assert
        ArtistFound exception = assertThrows(ArtistFound.class, () -> service.createArtist(request));
        assertTrue(exception.getMessage().contains("Last name there is an artist with the same last name"));
    }

    @Test
    void whenIdDoesNotExist_getArtistById_thenThrowNotFoundException() {
        // Arrange
        String artistId = "NON-EXISTENT";
        when(artistRepository.findAllByArtistIdentifier_ArtistId(artistId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getArtistById(artistId));
        assertEquals("Artist not found with id: " + artistId, exception.getMessage());
    }

    @Test
    void whenIdDoesNotExist_updateArtist_thenThrowNotFoundException() {
        // Arrange
        String artistId = "NON-EXISTENT";
        ArtistRequestModel request = new ArtistRequestModel();
        when(artistRepository.findAllByArtistIdentifier_ArtistId(artistId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.updateArtist(artistId, request));
    }

    @Test
    void whenUpdateRequestIsNull_updateArtist_thenThrowIllegalArgumentException() {
        // Arrange
        String artistId = "ART-123";
        when(artistRepository.findAllByArtistIdentifier_ArtistId(artistId)).thenReturn(new Artist());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.updateArtist(artistId, null));
        assertEquals("Artist cannot be null", exception.getMessage());
    }

    @Test
    void whenIdDoesNotExist_deleteArtist_thenThrowNotFoundException() {
        // Arrange
        String artistId = "NON-EXISTENT";
        when(artistRepository.findAllByArtistIdentifier_ArtistId(artistId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.deleteArtist(artistId));
    }

    @Test
    void whenLastNameExists_getArtistByLastName_shouldReturnArtist() {
        // Arrange
        String lastName = "Jennings";
        Artist artist = new Artist();
        artist.setLastName(lastName);

        ArtistResponseModel responseModel = new ArtistResponseModel();
        responseModel.setLastName(lastName);

        // Mock the repository to return the artist and the mapper to return the response model
        when(artistRepository.findAllByLastName(lastName)).thenReturn(artist);
        when(responseMapper.toRespondModel(artist)).thenReturn(responseModel);

        // Act
        ArtistResponseModel result = service.getArtistByLastName(lastName);

        // Assert
        assertNotNull(result);
        assertEquals(lastName, result.getLastName());
        verify(artistRepository, times(1)).findAllByLastName(lastName);
    }

    @Test
    void whenLastNameDoesNotExist_getArtistByLastName_thenThrowNotFoundException() {
        // Arrange
        String lastName = "Unknown";

        // Mock the repository to return null for a name that doesn't exist
        when(artistRepository.findAllByLastName(lastName)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getArtistByLastName(lastName)
        );

        // Verifying the specific error message defined in your implementation
        assertEquals("Artist not found with id: " + lastName, exception.getMessage());
    }
}