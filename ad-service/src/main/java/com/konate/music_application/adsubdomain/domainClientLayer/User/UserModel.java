package com.konate.music_application.adsubdomain.domainClientLayer.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel {
    String userId;
    String username;
    String email;
    String fullname;
    String age;
    String country;
//    String region;

}
