package com.konate.music_application.adsubdomain.PresentationLayer;

import com.konate.music_application.adsubdomain.DataLayer.AdCreative;
import com.konate.music_application.adsubdomain.DataLayer.AdTarget;
import com.konate.music_application.adsubdomain.DataLayer.CampaignStatus;
import com.konate.music_application.adsubdomain.DataLayer.TargetingRules;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdRequestModel {
    String name;
    BigDecimal budget;
    BigDecimal remainingSpend;
    CampaignStatus status;
    List<AdCreative> creatives;
    List<TargetingRules> targetingRules;

    //from the other services
    String userEmail;
//    String artistLastName;
//    String podcastTitle;

    AdTarget adTarget;
}
