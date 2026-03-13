package com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast;

import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastPricing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodcastRequestModel {
    String title;
    String hostname;
    String description;
    PodcastPricing pricingModel;
}
