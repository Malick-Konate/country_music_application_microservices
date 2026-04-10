package com.konate.music_application.podcastsubdomain.DataLayer.Episode;

import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastIdentifier;
//import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Time;
import java.util.Date;

@Document(collection = "episodes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
    @Id
    private String id;

    //    @Embedded
    @Transient
    private EpisodeIdentifier episodeIdentifier;

    @Field(name = "episodeId")
    private String episodeId;


    //    @Embedded
    @Transient
    private PodcastIdentifier podcastIdentifier;

    @Field(name = "podcastId")
    private String podcastId;

    @Field(name = "title")
    private String episodeTitle;

    @Field(name = "duration")
    private String duration;

    @Field(name = "publish_date")
    private Date publishDate;

    //    @Enumerated(EnumType.STRING)
    @Field(name = "status")
    private EpisodeStatus status;
}
