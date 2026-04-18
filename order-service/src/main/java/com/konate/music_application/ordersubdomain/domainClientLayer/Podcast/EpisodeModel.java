package com.konate.music_application.ordersubdomain.domainClientLayer.Podcast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpisodeModel {
    String episodeId;
    //    String hostname;
    String episodeTitle;
    Time duration;
    Date publishDate;
    EpisodeStatus status;

}
