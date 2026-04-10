package com.konate.music_application.podcastsubdomain.DataLayer.Podcast;


//import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
//import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "podcasts")
@Data
@NoArgsConstructor
public class Podcast {
    @Id
    private String id;

    //    @Embedded
    @Transient
    private PodcastIdentifier podcastIdentifier;

    @Field(name = "podcastId")
    private String podcastId;

    @Field(name = "title")
    private String title;

    @Field(name = "hostname")
    private String hostname;

    @Field(name = "description")
    private String description;

    //    @Enumerated(EnumType.STRING)
    @Field(name = "pricing_model")
    private PodcastPricing pricingModel;

    public Podcast(String title, String hostname,
                   String description, PodcastPricing pricing) {
        this.description = description;
        this.hostname = hostname;
        this.title = title;
        this.pricingModel = pricing;
    }
}
