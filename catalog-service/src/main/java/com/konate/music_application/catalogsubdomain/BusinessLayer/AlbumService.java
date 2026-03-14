package com.konate.music_application.catalogsubdomain.BusinessLayer;



import com.konate.music_application.catalogsubdomain.DataLayer.AlbumType;
import com.konate.music_application.catalogsubdomain.PresentationLayer.AlbumRequestModel;
import com.konate.music_application.catalogsubdomain.PresentationLayer.AlbumResponseModel;

import java.util.List;

public interface AlbumService {
    AlbumResponseModel getAlbumById(String albumId);

     List<AlbumResponseModel> getAllAlbums();

    AlbumResponseModel createAlbum(AlbumRequestModel album);

    AlbumResponseModel updateAlbum(String albumId, AlbumRequestModel album);

     void deleteAlbum(String albumId);

     AlbumResponseModel getAlbumByTitle(String title);

}
