package com.konate.music_application.podcastsubdomain.PresentationLayer.Podcast;

import com.konate.music_application.podcastsubdomain.DataLayer.Podcast.PodcastPricing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodcastResponseModel extends RepresentationModel<PodcastResponseModel> {
    String podcastId;
    String title;
    String hostname;
    String description;
    PodcastPricing pricingModel;
}
