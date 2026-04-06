package com.konate.music_application.podcastsubdomain.DataLayer.Podcast;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class PodcastIdentifier {
    @Field(name = "podcastId")
    private String podcastId;

    public PodcastIdentifier (){
        this.podcastId = "pod_country_" + java.util.UUID.randomUUID().toString();
    }

    public PodcastIdentifier(String podcastId){
        this.podcastId = podcastId;
    }
}
