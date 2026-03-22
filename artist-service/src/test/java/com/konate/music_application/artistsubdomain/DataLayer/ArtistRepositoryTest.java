package com.konate.music_application.artistsubdomain.DataLayer;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistRequestModel;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void whenArtistExist_thenReturnAllProfiles(){
        Artist artist = buildSampleArtist();
        Artist artist2 = buildSampleArtist();


        artistRepository.save(artist);
        artistRepository.save(artist2);

        long count = artistRepository.count();


        List<Artist> artists = artistRepository.findAll();
        assertNotNull(artists);
        assertNotEquals(0, count);
        assertEquals(count, artists.size());
        assertEquals(artist, artists.get(0));
        assertEquals(artist2, artists.get(1));
    }

    @Test
    public void whenArtistExist_thenReturnArtistByID(){
        Artist artist = buildSampleArtist();
        artistRepository.save(artist);

        Artist found = artistRepository.findAllByArtistIdentifier_ArtistId(artist.getArtistIdentifier().getArtistId());

        assertNotNull(found);
        assertEquals(artist.getGenres(), found.getGenres());
        assertEquals(artist.getSocialMediaLinks(), found.getSocialMediaLinks());
        assertEquals(artist.getBiography(), found.getBiography());
        assertEquals(artist.getFirstName(), found.getFirstName());
        assertEquals(artist.getLastName(), found.getLastName());
    }



    private Artist buildSampleArtist() {
        return Artist.builder()
                .artistIdentifier(new ArtistIdentifier())
                .firstName("Firstname")
                .lastName("Lastname")
                .biography(new ArtistBio("this is the biography"))
                .socialMediaLinks(List.of(
                        new SocialMediaLink("Instagram", "URI"),
                        new SocialMediaLink("Facebook", "URI")
                ))
                .genres(List.of(
                        new Genre("classic Country"),
                        new Genre("outlaw country")
                ))
                .build();
    }

}
