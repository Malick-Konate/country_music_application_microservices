package com.konate.music_application.catalogsubdomain.DataLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CatalogueRepositoryTest {
    @Autowired
    private AlbumRepository albumRepository;

    @BeforeEach
    public void setUp() {
        albumRepository.deleteAll();
    }

    @Test
    public void whenAlbumsExist_thenReturnAllAlbums() {
        // Arrange
        Album album1 = buildSampleAlbum("Whiskey Roads", "ALB-001");
        Album album2 = buildSampleAlbum("Southern Skies", "ALB-002");

        albumRepository.save(album1);
        albumRepository.save(album2);

        // Act
        List<Album> albums = albumRepository.findAll();

        // Assert
        assertNotNull(albums);
        assertEquals(2, albums.size());
    }

    @Test
    public void whenAlbumExists_thenReturnAlbumByAlbumId() {
        // Arrange
        String targetId = "ALB-999";
        Album album = buildSampleAlbum("Special Edition", targetId);
        albumRepository.save(album);

        // Act
        Album found = albumRepository.findAllByAlbumIdentifier_AlbumId(targetId);

        // Assert
        assertNotNull(found);
        assertEquals(targetId, found.getAlbumIdentifier().getAlbumId());
        assertEquals("Special Edition", found.getTitle());
    }

    @Test
    public void whenAlbumExists_thenReturnAlbumByTitle() {
        // Arrange
        String targetTitle = "Master of Puppets";
        Album album = buildSampleAlbum(targetTitle, "ALB-666");
        albumRepository.save(album);

        // Act
        Album found = albumRepository.findAllByTitle(targetTitle);

        // Assert
        assertNotNull(found);
        assertEquals(targetTitle, found.getTitle());
    }

    @Test
    public void whenAlbumDoesNotExist_thenReturnNull() {
        // Act
        Album foundById = albumRepository.findAllByAlbumIdentifier_AlbumId("NON-EXISTENT");
        Album foundByTitle = albumRepository.findAllByTitle("Non Existent Title");

        // Assert
        assertNull(foundById);
        assertNull(foundByTitle);
    }

    @Test
    public void whenSaveAlbum_thenSongsArePersisted() {
        // Arrange
        Album album = buildSampleAlbum("Collection Album", "ALB-100");

        // Act
        Album savedAlbum = albumRepository.save(album);
        Album found = albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-100");

        // Assert
        assertNotNull(found);
        assertNotNull(found.getSong());
        assertEquals(1, found.getSong().size());
        assertEquals("Track 1", found.getSong().get(0).getTitle());
    }


    @Test
    public void whenDeleteAlbum_thenNotFound() {
        // Arrange
        Album album = buildSampleAlbum("To Be Deleted", "ALB-000");
        Album saved = albumRepository.save(album);
        String savedAlbumId = saved.getAlbumIdentifier().getAlbumId();

        // Act
        albumRepository.delete(saved);
        Album found = albumRepository.findAllByAlbumIdentifier_AlbumId(savedAlbumId);

        // Assert
        assertNull(found, "Album should no longer exist in the database after deletion");
    }

    @Test
    public void whenUpdateAlbum_thenAttributesAreChanged() {
        // Arrange
        Album album = buildSampleAlbum("Original Title", "ALB-555");
        albumRepository.save(album);

        // Act
        Album existing = albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-555");
        existing.setTitle("Updated Title");
        existing.setRecordLabel("New Label");
        albumRepository.save(existing);

        Album updated = albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-555");

        // Assert
        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("New Label", updated.getRecordLabel());
    }

    @Test
    public void whenSaveWithCustomId_thenRetrieveByIdWorks() {
        // Arrange
        Album album = buildSampleAlbum("Custom ID Album", "CUSTOM-ID-123");
        // No need to set it again since buildSampleAlbum does it, but following Artist pattern:
        AlbumIdentifier customIdentifier = new AlbumIdentifier("ALB-CUSTOM-999");
        album.setAlbumIdentifier(customIdentifier);

        // Act
        albumRepository.save(album);
        Album found = albumRepository.findAllByAlbumIdentifier_AlbumId("ALB-CUSTOM-999");

        // Assert
        assertNotNull(found);
        assertEquals("ALB-CUSTOM-999", found.getAlbumIdentifier().getAlbumId());
        assertEquals("Custom ID Album", found.getTitle());
    }

    // Helper method to create sample data
    private Album buildSampleAlbum(String title, String albumId) {
        Album album = new Album();
        album.setAlbumIdentifier(new AlbumIdentifier(albumId));
        album.setTitle(title);
        album.setArtist_ID("ART-001");
        album.setReleaseDate(new Date());
        album.setRecordLabel("Sample Records");
        album.setAlbumType(AlbumType.LP);

        // Set up songs
        List<Song> songs = new ArrayList<>();
        songs.add(new Song("Track 1", Time.valueOf("00:03:45"), "Sample Lyrics"));
        album.setSong(songs);

        return album;
    }
}
