package com.konate.music_application.apigateway.BusinessLayer.Catalog;



import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumResponseModel;

import java.util.List;

public interface AlbumService {
    AlbumResponseModel getAlbumById(String albumId);

     List<AlbumResponseModel> getAllAlbums();

    AlbumResponseModel createAlbum(AlbumRequestModel album);

    AlbumResponseModel updateAlbum(String albumId, AlbumRequestModel album);

     void deleteAlbum(String albumId);

     AlbumResponseModel getAlbumByTitle(String title);

}
