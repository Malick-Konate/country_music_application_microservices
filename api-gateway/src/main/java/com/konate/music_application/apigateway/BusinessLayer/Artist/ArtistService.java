package com.konate.music_application.apigateway.BusinessLayer.Artist;




import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistResponseModel;

import java.util.List;

public interface ArtistService {
    List<ArtistResponseModel> getAllArtists();

    ArtistResponseModel getArtistById(String id);

    ArtistResponseModel createArtist(ArtistRequestModel artist);

    ArtistResponseModel updateArtist(String id, ArtistRequestModel artist);

    void deleteArtist(String id);

    ArtistResponseModel getArtistByLastName(String lastName);
}
