package com.konate.music_application.apigateway.BusinessLayer.Artist;

import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Artist.ArtistServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService{
    private final ArtistServiceClient artistServiceClient;

    public ArtistServiceImpl(ArtistServiceClient artistServiceClient) {
        this.artistServiceClient = artistServiceClient;
    }

    @Override
    public ArtistResponseModel getArtistById(String artistId) {
        return artistServiceClient.getArtistById(artistId);
    }

    @Override
    public ArtistResponseModel createArtist(ArtistRequestModel artist) {
        return artistServiceClient.createArtist(artist);
    }

    @Override
    public ArtistResponseModel updateArtist(String artistId, ArtistRequestModel artist) {
        return artistServiceClient.updateArtist(artistId, artist);
    }

    @Override
    public void deleteArtist(String artistId) {
        artistServiceClient.deleteArtist(artistId);
    }

    @Override
    public List<ArtistResponseModel> getAllArtists() {
        return artistServiceClient.getAllArtist();
    }

    @Override
    public ArtistResponseModel getArtistByLastName(String lastName) {
        return artistServiceClient.getArtistByLastName(lastName);
    }

}
