package com.konate.music_application.artistsubdomain.BusinessLayer;

import com.konate.music_application.artistsubdomain.DataLayer.*;
import com.konate.music_application.artistsubdomain.Exceptions.ArtistFound;
import com.konate.music_application.artistsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.artistsubdomain.MappingLayer.ArtistRequestMapper;
import com.konate.music_application.artistsubdomain.MappingLayer.ArtistResponseMapper;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistRequestModel;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtistResponseMapper artistResponseMapper;
    private final ArtistRequestMapper artistRequestMapper;

    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistRequestMapper requestMapper, ArtistResponseMapper responseMapper) {
        this.artistRepository = artistRepository;
        this.artistResponseMapper = responseMapper;
        this.artistRequestMapper = requestMapper;
    }

    @Override
    public List<ArtistResponseModel> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        return artistResponseMapper.entityListToRespondModel(artists);
    }

    @Override
    public ArtistResponseModel getArtistById(String id) {
//        Artist artist = artistRepository.findById(id).orElseThrow(() -> new NotFoundException("Artist not found with id: " + id));
        Artist artist = artistRepository.findAllByArtistIdentifier_ArtistId(id);
        if (artist == null) {
            throw new NotFoundException("Artist not found with id: " + id);
        }
        return artistResponseMapper.toRespondModel(artist);
    }

    @Override
    public ArtistResponseModel createArtist(ArtistRequestModel artist) {
        if (artist == null) {
            throw new IllegalArgumentException("Invalid input: Artist request model cannot be null");
        }
        Artist compare = artistRepository.findAllByLastName(artist.getLastName());
        if (compare != null) {
            throw  new ArtistFound("Last name there is an artist with the same last name: "
                    + compare.getFirstName() + " " + compare.getLastName());
        }
//        List<SocialMediaLink> socialMediaLinks = artist.getSocialMediaLinks();
//        if (socialMediaLinks != null && !socialMediaLinks.isEmpty()) {
//            artist.setSocialMediaLinks(socialMediaLinks);
//        }
//        List<Genre> genres = artist.getGenres();
//        if (genres != null && !genres.isEmpty()) {
//            artist.setGenres(genres);
//        }

        Artist artistEntity = artistRequestMapper.toEntity(
                artist,
                new ArtistIdentifier(),
                new ArtistBio(artist.getBiography())
        );

        Artist savedArtist = artistRepository.save(artistEntity);
        return artistResponseMapper.toRespondModel(savedArtist);
    }

    @Override
    public ArtistResponseModel updateArtist(String id, ArtistRequestModel artist) {
        Artist artistExisting = artistRepository.findAllByArtistIdentifier_ArtistId(id);

        if (artistExisting == null)
            throw new NotFoundException("Artist not found with id: " + id);
        if (artist == null) {
            throw new IllegalArgumentException("Artist cannot be null");
        }
        artistExisting.setFirstName(artist.getFirstName());
        artistExisting.setLastName(artist.getLastName());
        List<SocialMediaLink> socialMediaLinks = artist.getSocialMediaLinks();
        if (socialMediaLinks != null && !socialMediaLinks.isEmpty()) {
            List<SocialMediaLink> existingLinks = artistExisting.getSocialMediaLinks();

            artistExisting.setSocialMediaLinks(existingLinks);

        }
        List<Genre> genres = artist.getGenres();
        if (genres != null && !genres.isEmpty()) {
            artistExisting.setGenres(genres);
        }

        artistExisting.setBiography(new ArtistBio(artist.getBiography()));
//        artistExisting.getSocialMediaLinks().add(new SocialMediaLink(artist.getSocialMediaLinks().get(0).getURI()));
        Artist updatedArtist = artistRepository.save(artistExisting);
        return artistResponseMapper.toRespondModel(updatedArtist);
    }

    @Override
    public void deleteArtist(String id) {
        Artist artist = artistRepository.findAllByArtistIdentifier_ArtistId(id);
        if (artist == null) {
            throw new NotFoundException("Artist not found with id: " + id);
        }
        artistRepository.delete(artist);
    }

    @Override
    public ArtistResponseModel getArtistByLastName(String lastName) {
        Artist artist = artistRepository.findAllByLastName(lastName);
        if (artist == null) {
            throw new NotFoundException("Artist not found with id: " + lastName);
        }
        return artistResponseMapper.toRespondModel(artist);
    }
}
