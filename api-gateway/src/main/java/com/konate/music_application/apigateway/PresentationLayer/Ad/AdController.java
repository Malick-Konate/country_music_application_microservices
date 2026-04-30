package com.konate.music_application.apigateway.PresentationLayer.Ad;

import com.konate.music_application.apigateway.BusinessLayer.ad.AdCampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/ads")
public class AdController {
    private final AdCampaignService adCampaignService;

    public AdController(AdCampaignService adCampaignService) {
        this.adCampaignService = adCampaignService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<AdResponseModel>> getAllAdCampaigns() {
        log.info("Getting all ad campaigns");
        return new ResponseEntity<>(adCampaignService.getAllAdCampaigns(), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{adId}",
            produces = "application/json"
    )
    public ResponseEntity<AdResponseModel> getAdCampaignById(@PathVariable String adId) {
        log.info("Getting ad campaign by id: {}", adId);
        return new ResponseEntity<>(adCampaignService.getAdCampaign(adId), HttpStatus.OK);
    }

    @PatchMapping(
            value = "/{adId}/activate",
//            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> activateAdCampaign(@PathVariable String adId) {
        log.info("Activating ad campaign by id: {}", adId);
        adCampaignService.activateAdCampaign(adId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(
            value = "/{adId}/pause",
//            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> pauseCampaign(@PathVariable String adId) {
        log.info("Deactivating ad campaign by id: {}", adId);
        adCampaignService.pauseAdCampaign(adId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(
            value = "/{adId}/resume",
//            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> resumeCampaign(@PathVariable String adId) {
        log.info("Resuming ad campaign by id: {}", adId);
        adCampaignService.resumeAdCampaign(adId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(
            value = "/create",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AdResponseModel> createAdCampaign(@RequestBody AdRequestModel requestModel) {
        log.info("Creating ad campaign: {}", requestModel);
        return new ResponseEntity<>(adCampaignService.createAdCampaign(requestModel), HttpStatus.CREATED);
    }

    @PutMapping(
            value = "/{adId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AdResponseModel> updateAdCampaign(@PathVariable String adId, @RequestBody AdRequestModel requestModel) {
        log.info("Updating ad campaign by id: {}", adId);
        return new ResponseEntity<>(adCampaignService.updateAdCampaign(adId, requestModel), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/{adId}",
            produces = "application/json"
    )
    public ResponseEntity<HttpStatus> deleteAdCampaign(@PathVariable String adId) {
        log.info("Deleting ad campaign by id: {}", adId);
        adCampaignService.deleteAdCampaign(adId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
