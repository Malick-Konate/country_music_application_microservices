package com.konate.music_application.artistsubdomain.BusinessLayer;



import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistRequestModel;
import com.konate.music_application.artistsubdomain.PresentationLayer.ArtistResponseModel;

import java.util.List;

public interface ArtistService {
    List<ArtistResponseModel> getAllArtists();

    ArtistResponseModel getArtistById(String id);

    ArtistResponseModel createArtist(ArtistRequestModel artist);

    ArtistResponseModel updateArtist(String id, ArtistRequestModel artist);

    void deleteArtist(String id);

    ArtistResponseModel getArtistByLastName(String lastName);
}
