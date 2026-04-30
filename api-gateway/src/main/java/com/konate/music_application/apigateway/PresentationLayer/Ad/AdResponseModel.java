package com.konate.music_application.apigateway.PresentationLayer.Ad;

import com.konate.music_application.apigateway.domainClientLayer.Ad.AdCreative;
import com.konate.music_application.apigateway.domainClientLayer.Ad.CampaignStatus;
import com.konate.music_application.apigateway.domainClientLayer.Ad.TargetingRules;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdResponseModel extends RepresentationModel<AdResponseModel> {
    String ad_id;
    String name;
    BigDecimal budget;
    BigDecimal remainingSpend;
    CampaignStatus status;
    List<AdCreative> creatives;
    List<TargetingRules> targetingRules;

    //from the other services
    String userName;
//    String artistName;
    String targetName;
    String targetHostnameOrArtist;
}
