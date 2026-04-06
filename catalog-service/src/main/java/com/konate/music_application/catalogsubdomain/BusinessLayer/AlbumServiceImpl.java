package com.konate.music_application.catalogsubdomain.BusinessLayer;

import com.konate.music_application.catalogsubdomain.DataLayer.Album;
import com.konate.music_application.catalogsubdomain.DataLayer.AlbumIdentifier;
import com.konate.music_application.catalogsubdomain.DataLayer.AlbumRepository;
import com.konate.music_application.catalogsubdomain.DataLayer.Song;
import com.konate.music_application.catalogsubdomain.Exceptions.InconsistentAlbumException;
import com.konate.music_application.catalogsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.catalogsubdomain.MappingLayer.AlbumRequestMapper;
import com.konate.music_application.catalogsubdomain.MappingLayer.AlbumResponseMapper;
import com.konate.music_application.catalogsubdomain.PresentationLayer.AlbumRequestModel;
import com.konate.music_application.catalogsubdomain.PresentationLayer.AlbumResponseModel;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistModel;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistServiceClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumResponseMapper albumResponseMapper;
    private final AlbumRequestMapper albumRequestMapper;
    //    private final ArtistRequestMapper artistRequestMapper;
//    private final ArtistService artistService;
    private final ArtistServiceClient artistServiceClient;

    public AlbumServiceImpl(AlbumRepository albumRepository, AlbumResponseMapper albumResponseMapper, AlbumRequestMapper albumRequestMapper, ArtistServiceClient artistServiceClient) {
        this.albumRepository = albumRepository;
        this.albumResponseMapper = albumResponseMapper;
        this.albumRequestMapper = albumRequestMapper;
        this.artistServiceClient = artistServiceClient;
//        this.artistRequestMapper = artistRequestMapper;
//        this.artistService = artistService;
    }


    @Override
    public AlbumResponseModel getAlbumById(String albumId) {

        Album album = albumRepository.findAllByAlbumIdentifier_AlbumId(albumId);
        if (album == null) {
            throw new NotFoundException("Album not found with id: " + albumId);
        }
        ArtistModel artist = artistServiceClient.getArtistById(album.getArtist_ID());

        AlbumResponseModel albumResponseModel = albumResponseMapper.toResponseModel(album);
        albumResponseModel.setArtistFirstName(artist.getFirstName());
        albumResponseModel.setArtistLastName(artist.getLastName());

        return albumResponseModel;
    }

    @Override
    public List<AlbumResponseModel> getAllAlbums() {
        List<Album> albums = albumRepository.findAll();
        List<AlbumResponseModel> respondModel = new ArrayList<>();
        for (Album album : albums) {
            ArtistModel artist = artistServiceClient.getArtistById(album.getArtist_ID());

            AlbumResponseModel albumResponseModel = albumResponseMapper.toResponseModel(album);
            albumResponseModel.setArtistFirstName(artist.getFirstName());
            albumResponseModel.setArtistLastName(artist.getLastName());

            respondModel.add(albumResponseModel);
        }


        return respondModel;
    }

    @Override
    public AlbumResponseModel createAlbum(AlbumRequestModel album) {
        ArtistModel artist = artistServiceClient.getArtistById(album.getArtistId());
        if (artist == null) {
            throw new NotFoundException("Artist not found with id: " + album.getArtistId());
        }

        List<Song> songs = album.getSong();
        if (songs == null || songs.isEmpty()) {
            throw new InconsistentAlbumException("Album must have at least one song");
        }

        Album albumEntity = albumRequestMapper.toAlbum(
                album,
                new AlbumIdentifier(),
                artist
        );


        Album savedAlbum = albumRepository.save(albumEntity);
        AlbumResponseModel albumResponseModel = albumResponseMapper.toResponseModel(savedAlbum);
        albumResponseModel.setArtistLastName(artist.getLastName());
        albumResponseModel.setArtistFirstName(artist.getFirstName());


        return albumResponseModel;
    }

    @Override
    public AlbumResponseModel updateAlbum(String albumId, AlbumRequestModel album) {
        Album existingAlbum = albumRepository.findAllByAlbumIdentifier_AlbumId(albumId);
        ArtistModel artist = artistServiceClient.getArtistById(album.getArtistId());

        if (existingAlbum == null) {
            throw new NotFoundException("Album not found with id: " + albumId);
        }
        if (album == null) {
            throw new IllegalArgumentException("Album cannot be null");
        }
        existingAlbum.setTitle(album.getTitle());
        existingAlbum.setAlbumType(album.getAlbumType());
        existingAlbum.setReleaseDate(album.getReleaseDate());
        existingAlbum.setRecordLabel(album.getRecordLabel());
//        existingAlbum.setArtistIdentifier(new ArtistIdentifier(album.getArtistId()));

        Album savedAlbum = albumRepository.save(existingAlbum);

        AlbumResponseModel albumResponseModel = albumResponseMapper.toResponseModel(savedAlbum);
        albumResponseModel.setArtistLastName(artist.getLastName());
        albumResponseModel.setArtistFirstName(artist.getFirstName());
        return albumResponseModel;
    }

    @Override
    public void deleteAlbum(String albumId) {
        Album album = albumRepository.findAllByAlbumIdentifier_AlbumId(albumId);
        if (album == null) {
            throw new NotFoundException("Album not found with id: " + albumId);
        }
        albumRepository.delete(album);
    }

    @Override
    public AlbumResponseModel getAlbumByTitle(String title) {
        Album album = albumRepository.findAllByTitle(title);
        if (album == null) {
            throw new NotFoundException("Album not found with title: " + title);
        }
        ArtistModel artist = artistServiceClient.getArtistById(album.getArtist_ID());

        AlbumResponseModel albumResponseModel = albumResponseMapper.toResponseModel(album);
        albumResponseModel.setArtistFirstName(artist.getFirstName());
        albumResponseModel.setArtistLastName(artist.getLastName());

        return albumResponseModel;
    }

}
