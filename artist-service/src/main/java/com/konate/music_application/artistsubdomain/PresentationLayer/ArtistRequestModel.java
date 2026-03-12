package com.konate.music_application.artistsubdomain.PresentationLayer;

import com.konate.music_application.artistsubdomain.DataLayer.Genre;
import com.konate.music_application.artistsubdomain.DataLayer.SocialMediaLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistRequestModel {
    private String firstName;
    private String lastName;
    private List<Genre> genres;
    private List<SocialMediaLink> socialMediaLinks;
    private String biography;

}
