package com.konate.music_application.apigateway.BusinessLayer.ad;

import com.konate.music_application.apigateway.PresentationLayer.Ad.AdRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Ad.AdServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdServiceImpl implements AdCampaignService{
    private final AdServiceClient serviceClient;

    public AdServiceImpl(AdServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Override
    public AdResponseModel createAdCampaign(AdRequestModel adRequestModel) {
        return serviceClient.createAd(adRequestModel);
    }

    @Override
    public AdResponseModel updateAdCampaign(String adId, AdRequestModel adRequestModel) {
        return serviceClient.updateAd(adId, adRequestModel);
    }

    @Override
    public void deleteAdCampaign(String adId) {
        serviceClient.deleteAd(adId);
    }

    @Override
    public AdResponseModel getAdCampaign(String adId) {
        return serviceClient.getAdById(adId);
    }

    @Override
    public List<AdResponseModel> getAllAdCampaigns() {
        return serviceClient.getAllAds();
    }

    @Override
    public AdResponseModel getAdCampaignsByUser(String userEmail) {
        return null;
    }

    @Override
    public void activateAdCampaign(String adId) {
        serviceClient.activateAd(adId);
    }

    @Override
    public void pauseAdCampaign(String adId) {
        serviceClient.pauseAd(adId);
    }

    @Override
    public void resumeAdCampaign(String adId) {
        serviceClient.resumeAd(adId);
    }
}
