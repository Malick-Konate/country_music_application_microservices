package com.konate.music_application.podcastsubdomain.DataLayer.Episode;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import static java.util.UUID.randomUUID;

//@Embeddable
@Getter
public class EpisodeIdentifier {
    @Field(name = "episodeId")
    private String episodeId;

    public EpisodeIdentifier(){
        this.episodeId = "ep_" + randomUUID().toString();
    }

    public EpisodeIdentifier(String episodeId){
        this.episodeId = episodeId;
    }
}
