package com.konate.music_application.adsubdomain.domainClientLayer.Artist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistModel {
    private String artistIdentifier;
    private String firstName;
    private String lastName;
    private List<Genre> genres;
    private List<SocialMediaLink> socialMediaLinks;
    private String biography;
}
