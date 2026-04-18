package com.konate.music_application.ordersubdomain.domainClientLayer.Podcast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PodcastModel {
    String podcastId;
    String title;
    String hostname;
    String description;
    PodcastPricing pricingModel;
}
