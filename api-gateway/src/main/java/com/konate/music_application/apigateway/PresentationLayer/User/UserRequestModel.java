package com.konate.music_application.apigateway.PresentationLayer.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestModel {
    String username;
    String email;
    String password;
    String fullname;
    int age;
    String country;
//    String region;
}
