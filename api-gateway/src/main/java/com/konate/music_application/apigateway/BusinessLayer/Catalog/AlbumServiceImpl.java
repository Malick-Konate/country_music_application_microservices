package com.konate.music_application.apigateway.BusinessLayer.Catalog;

import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Catalog.CatalogServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService{
    private final CatalogServiceClient serviceClient;

    public AlbumServiceImpl(CatalogServiceClient serviceClient) {
        this.serviceClient = serviceClient;

    }

    @Override
    public AlbumResponseModel getAlbumById(String albumId) {
        return serviceClient.getAlbum(albumId);
    }

    @Override
    public List<AlbumResponseModel> getAllAlbums() {
        return serviceClient.getAllAlbums();
    }

    @Override
    public AlbumResponseModel createAlbum(AlbumRequestModel album) {
        return serviceClient.createAlbum(album);
    }

    @Override
    public AlbumResponseModel updateAlbum(String albumId, AlbumRequestModel album) {
        return serviceClient.updateAlbum(albumId, album);
    }

    @Override
    public void deleteAlbum(String albumId) {
        serviceClient.deleteAlbum(albumId);
    }

    @Override
    public AlbumResponseModel getAlbumByTitle(String title) {
        return null;
    }
}
