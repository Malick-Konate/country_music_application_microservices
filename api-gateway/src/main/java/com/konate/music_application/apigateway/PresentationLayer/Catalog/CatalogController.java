package com.konate.music_application.apigateway.PresentationLayer.Catalog;

import com.konate.music_application.apigateway.BusinessLayer.Catalog.AlbumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/album")
public class CatalogController {
    private final AlbumService albumService;

    public CatalogController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<AlbumResponseModel>> getAllAlbums() {
        return new ResponseEntity<>(albumService.getAllAlbums(), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{albumId}",
            produces = "application/json"
    )
    public ResponseEntity<AlbumResponseModel> getAlbumById(@PathVariable String albumId) {
        return new ResponseEntity<>(albumService.getAlbumById(albumId), HttpStatus.OK);
    }

    @PostMapping(
            value = "/create",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AlbumResponseModel> createAlbum(@RequestBody AlbumRequestModel album) {
        return new ResponseEntity<>(albumService.createAlbum(album), HttpStatus.CREATED);
    }

    @PutMapping(
            value = "/update/{albumId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AlbumResponseModel> updateAlbum(@PathVariable String albumId, @RequestBody AlbumRequestModel album) {
        return new ResponseEntity<>(albumService.updateAlbum(albumId, album), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/delete/{albumId}",
            produces = "application/json"
    )
    public ResponseEntity<Void> deleteAlbum(@PathVariable String albumId) {
        albumService.deleteAlbum(albumId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }





}
