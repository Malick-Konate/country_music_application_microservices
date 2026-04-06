package com.konate.music_application.podcastsubdomain.MappingLayer.Episode;

//import org.mapstruct.Mapper;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.Episode;
import com.konate.music_application.podcastsubdomain.DataLayer.Episode.EpisodeIdentifier;
import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastIdentifier;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeRequestModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.Episode.EpisodeResponseModel;
import com.konate.music_application.podcastsubdomain.PresentationLayer.PodcastController;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EpisodeRequestMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
//            @Mapping(expression = "java(podcastIdentifier)", target = "podcastIdentifier"),
//            @Mapping(expression = "java(episodeIdentifier)", target = "episodeIdentifier"),
            @Mapping(expression = "java(episodeIdentifier.getEpisodeId())", target = "episodeId"),
            @Mapping(expression = "java(podcastIdentifier.getPodcastId())", target = "podcastId"),
            @Mapping(source = "requestModel.episodeTitle", target = "episodeTitle"),
//            @Mapping(source = "requestModel.duration", target = "duration"),
            @Mapping(expression = "java(requestModel.getDuration().toString())", target = "duration"),
            @Mapping(source = "requestModel.publishDate", target = "publishDate"),
            @Mapping(source = "requestModel.status", target = "status")
    })
    Episode toEntity(EpisodeRequestModel requestModel,
                     EpisodeIdentifier episodeIdentifier,
                     PodcastIdentifier podcastIdentifier);
}
