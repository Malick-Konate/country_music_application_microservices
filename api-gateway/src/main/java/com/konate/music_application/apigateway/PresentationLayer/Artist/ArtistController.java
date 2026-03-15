package com.konate.music_application.apigateway.PresentationLayer.Artist;

import com.konate.music_application.apigateway.BusinessLayer.Artist.ArtistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/artists")
public class ArtistController {
    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<ArtistResponseModel>> getAllArtists() {
        return new ResponseEntity<>(artistService.getAllArtists(), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{artistId}",
            produces = "application/json"
    )
    public ResponseEntity<ArtistResponseModel> getArtist(@PathVariable String artistId) {
        return new ResponseEntity<>(artistService.getArtistById(artistId), HttpStatus.OK);
    }
    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ArtistResponseModel> createArtist(@RequestBody ArtistRequestModel artist) {
        return new ResponseEntity<>(artistService.createArtist(artist), HttpStatus.CREATED);
    }
    @PutMapping(
            value = "/{artistId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ArtistResponseModel> updateArtist(@PathVariable String artistId, @RequestBody ArtistRequestModel artist) {
        return new ResponseEntity<>(artistService.updateArtist(artistId, artist), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/{artistId}",
            produces = "application/json"
    )
    public ResponseEntity<Void> deleteArtist(@PathVariable String artistId) {
        artistService.deleteArtist(artistId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
