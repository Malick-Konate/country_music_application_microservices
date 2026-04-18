package com.konate.music_application.ordersubdomain.domainClientLayer.Catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumModel {
    private String albumId;
    private String title;
    //    private String artistId;
    private String artistFirstName;
    private String artistLastName;
    private String releaseDate;
    private AlbumType albumType;
    //    private String songTitle;
//    private Time songDuration;
//    private String songLyrics;
    private String recordLabel;

    private List<Song> song;

}
