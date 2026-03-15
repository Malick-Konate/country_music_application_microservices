package com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast;

import com.konate.music_application.apigateway.domainClientLayer.Podcast.PodcastPricing;
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
