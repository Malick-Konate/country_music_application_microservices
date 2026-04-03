package com.konate.music_application.artistsubdomain.MappingLayer;

import com.konate.music_application.artistsubdomain.DataLayer.*;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistRequestModel;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistResponseModel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistMapperTest {

    private final ArtistRequestMapper requestMapper =
            Mappers.getMapper(ArtistRequestMapper.class);

    private final ArtistResponseMapper responseMapper =
            Mappers.getMapper(ArtistResponseMapper.class);

    @Test
    void toEntity_shouldMapAllFields() {
        ArtistRequestModel request = new ArtistRequestModel();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBiography("Bio");

        ArtistIdentifier identifier = new ArtistIdentifier("ID-1");
        ArtistBio bio = new ArtistBio("Bio");

        Artist entity = requestMapper.toEntity(request, identifier, bio);

        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals(identifier, entity.getArtistIdentifier());
        assertEquals("Bio", entity.getBiography().getText());
    }

    @Test
    void toResponseModel_shouldMapCorrectly() {
        Artist artist = new Artist();
        artist.setArtistIdentifier(new ArtistIdentifier("ID-1"));
        artist.setFirstName("John");
        artist.setLastName("Doe");
        artist.setBiography(new ArtistBio("Bio"));

        ArtistResponseModel response = responseMapper.toRespondModel(artist);

        assertEquals("ID-1", response.getArtistIdentifier());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Bio", response.getBiography());


        assertNotNull(response);
    }

    @Test
    void entityListToRespondModel_shouldMapList() {
        Artist artist = new Artist();
        artist.setArtistIdentifier(new ArtistIdentifier("ID-1"));

        var result = responseMapper.entityListToRespondModel(List.of(artist));

        assertEquals(1, result.size());
    }

    @Test
    void toEntity_withNullFields_shouldStillMap() {
        ArtistRequestModel request = new ArtistRequestModel();

        Artist entity = requestMapper.toEntity(
                request,
                new ArtistIdentifier("ID-1"),
                new ArtistBio(null)
        );

        assertNotNull(entity);
        assertEquals("ID-1", entity.getArtistIdentifier().getArtistId());
    }

    @Test
    void entityListToRespondModel_withEmptyList_shouldReturnEmptyList() {
        List<ArtistResponseModel> result =
                responseMapper.entityListToRespondModel(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toResponseModel_withCollections_shouldMapCollections() {
        Artist artist = new Artist();
        artist.setArtistIdentifier(new ArtistIdentifier("ID-1"));
        artist.setGenres(List.of(new Genre("Rock")));
        artist.setSocialMediaLinks(List.of(new SocialMediaLink("IG", "url")));
        artist.setBiography(new ArtistBio("Bio"));

        ArtistResponseModel response = responseMapper.toRespondModel(artist);

        assertEquals(1, response.getGenres().size());
        assertEquals(1, response.getSocialMediaLinks().size());
    }
}