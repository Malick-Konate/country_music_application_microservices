package com.konate.music_application.apigateway.BusinessLayer.User;



import com.konate.music_application.apigateway.PresentationLayer.User.UserRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.User.UserResponseModel;

import java.util.List;

public interface UserService {
    UserResponseModel registerUser(UserRequestModel userRequestModel);

    void deleteUser(String username);

    UserResponseModel updateUser(String username, UserRequestModel userRequestModel);

    List<UserResponseModel> getAllUsers();

    UserResponseModel getUserByUsername(String username);

//    UserResponseModel changeUserPassword(String username, UserRequestModel model);

    UserResponseModel getUserByEmail(String email);

    UserResponseModel getUserById(String id);

//    String authenticateUser(String username, String password);
//
//    void revokeRoleFromUser(String username, String role);
//
//    boolean checkUserPermission(String username, String permission);
}
