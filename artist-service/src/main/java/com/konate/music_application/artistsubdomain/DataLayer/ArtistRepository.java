package com.konate.music_application.artistsubdomain.DataLayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    Artist findAllByArtistIdentifier_ArtistId(String artistId);
    Artist findAllByFirstName(String firstName);
    Artist findAllByLastName(String lastName);
//    Artist findAllByEmail(String email);
}
