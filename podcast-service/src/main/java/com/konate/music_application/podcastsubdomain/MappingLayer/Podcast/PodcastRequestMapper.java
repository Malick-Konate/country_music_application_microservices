package com.konate.music_application.podcastsubdomain.MappingLayer.Podcast;

import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.Podcast;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastIdentifier;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast.PodcastRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")

public interface PodcastRequestMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(expression = "java(podcastIdentifier)", target = "podcastIdentifier"),
            @Mapping(source = "requestModel.title", target = "title"),
            @Mapping(source = "requestModel.hostname", target = "hostname"),
            @Mapping(source = "requestModel.description", target = "description"),
            @Mapping(source = "requestModel.pricingModel", target = "pricingModel"),

    })
    Podcast toPodcast(PodcastRequestModel requestModel,
                      PodcastIdentifier podcastIdentifier
                      );
}
