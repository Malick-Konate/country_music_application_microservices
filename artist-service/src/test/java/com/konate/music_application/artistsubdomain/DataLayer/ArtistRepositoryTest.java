package com.konate.music_application.artistsubdomain.DataLayer;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @BeforeEach
    public void setUp() {
        artistRepository.deleteAll();
    }

    @Test
    public void whenArtistExist_thenReturnAllProfiles() {
        // Arrange
        Artist artist = buildSampleArtist();
        Artist artist2 = buildSampleArtist();

        artistRepository.save(artist);
        artistRepository.save(artist2);

        long count = artistRepository.count();

        // Act
        List<Artist> artists = artistRepository.findAll();

        // Assert
        assertNotNull(artists);
        assertNotEquals(0, count);
        assertEquals(count, artists.size());
        assertEquals(artist, artists.get(0));
        assertEquals(artist2, artists.get(1));
    }

    @Test
    public void whenArtistExist_thenReturnArtistByID() {
        // Arrange
        Artist artist = buildSampleArtist();
        artistRepository.save(artist);

        // Act
        Artist found = artistRepository.findAllByArtistIdentifier_ArtistId(artist.getArtistIdentifier().getArtistId());

        // Assert
        assertNotNull(found);
        assertEquals(artist.getGenres(), found.getGenres());
        assertEquals(artist.getSocialMediaLinks(), found.getSocialMediaLinks());
        assertEquals(artist.getBiography(), found.getBiography());
        assertEquals(artist.getFirstName(), found.getFirstName());
        assertEquals(artist.getLastName(), found.getLastName());
    }

    @Test
    public void whenArtistDoesNotExist_thenReturnNull() {
        // Act
        Artist foundArtist = artistRepository.findAllByArtistIdentifier_ArtistId("non-existing-id");

        // Assert
        assertNull(foundArtist, "Repository should return null when the artist ID does not exist");
    }

    @Test
    public void whenValidArtistSaved_thenPersistAndReturnCorrectData() {
        // Arrange
        Artist artist = buildSampleArtist();

        // Act
        Artist savedArtist = artistRepository.save(artist);

        // Assert
        assertNotNull(savedArtist);
        assertNotNull(savedArtist.getId(), "Database should generate a primary key ID");
        assertNotNull(savedArtist.getArtistIdentifier());
        assertNotNull(savedArtist.getArtistIdentifier().getArtistId(), "ArtistIdentifier should be generated");

        assertEquals(artist.getFirstName(), savedArtist.getFirstName());
        assertEquals(artist.getLastName(), savedArtist.getLastName());
        assertEquals(artist.getBiography(), savedArtist.getBiography());

        assertEquals(2, savedArtist.getGenres().size(), "Should persist exactly 2 genres");
        assertEquals(2, savedArtist.getSocialMediaLinks().size(), "Should persist exactly 2 social media links");
    }

    @Test
    public void whenArtistUpdated_thenSaveAndReturnUpdatedData() {
        // Arrange
        Artist artist = buildSampleArtist();
        Artist savedArtist = artistRepository.save(artist);

        // Act
        savedArtist.setFirstName("Waylon");
        savedArtist.setLastName("Jennings");
        Artist updatedArtist = artistRepository.save(savedArtist);

        // Assert
        assertNotNull(updatedArtist);
        assertEquals("Waylon", updatedArtist.getFirstName());
        assertEquals("Jennings", updatedArtist.getLastName());
        assertEquals(savedArtist.getArtistIdentifier().getArtistId(), updatedArtist.getArtistIdentifier().getArtistId(), "Artist ID should remain the same after an update");
    }

    @Test
    public void whenArtistDeleted_thenCannotBeFound() {
        // Arrange
        Artist artist = buildSampleArtist();
        Artist savedArtist = artistRepository.save(artist);
        String savedArtistId = savedArtist.getArtistIdentifier().getArtistId();

        // Act
        artistRepository.delete(savedArtist);
        Artist found = artistRepository.findAllByArtistIdentifier_ArtistId(savedArtistId);

        // Assert
        assertNull(found, "Artist should no longer exist in the database after deletion");
    }

    @Test
    void whenSaveWithCustomId_thenRetrieveByIdWorks() {
        // Arrange
        Artist artist = buildSampleArtist();
        ArtistIdentifier customIdentifier = new ArtistIdentifier("ARTIST-999");
        artist.setArtistIdentifier(customIdentifier);

        // Act
        artistRepository.save(artist);
        Artist found = artistRepository.findAllByArtistIdentifier_ArtistId("ARTIST-999");

        // Assert
        assertNotNull(found);
        assertEquals("ARTIST-999", found.getArtistIdentifier().getArtistId());
    }

    // Helper method to create sample data
    private Artist buildSampleArtist() {
        return Artist.builder()
                .artistIdentifier(new ArtistIdentifier())
                .firstName("Firstname")
                .lastName("Lastname")
                .biography(new ArtistBio("this is the biography"))
                .socialMediaLinks(new ArrayList<>(List.of(
                        new SocialMediaLink("Instagram", "URI"),
                        new SocialMediaLink("Facebook", "URI")
                )))
                .genres(new ArrayList<>(List.of(
                        new Genre("classic Country"),
                        new Genre("outlaw country")
                )))
                .build();
    }}