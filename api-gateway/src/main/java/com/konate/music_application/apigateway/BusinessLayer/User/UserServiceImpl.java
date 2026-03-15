package com.konate.music_application.apigateway.BusinessLayer.User;

import com.konate.music_application.apigateway.PresentationLayer.User.UserRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.User.UserResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.User.UserServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserServiceClient serviceClient;

    public UserServiceImpl(UserServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Override
    public UserResponseModel registerUser(UserRequestModel userRequestModel) {

        return serviceClient.createUser(userRequestModel);
    }

    @Override
    public void deleteUser(String username) {
        serviceClient.deleteUser(username);
    }

    @Override
    public UserResponseModel updateUser(String username, UserRequestModel userRequestModel) {
        return serviceClient.updateUser(username, userRequestModel);
    }

    @Override
    public List<UserResponseModel> getAllUsers() {
        return serviceClient.getAllUsers();
    }

    @Override
    public UserResponseModel getUserByUsername(String username) {
        return serviceClient.getUserByUsername(username);
    }

//    @Override
//    public UserResponseModel changeUserPassword(String username, UserRequestModel model) {
//        return serviceClient.changeUserPassword(username, model);
//    }

    @Override
    public UserResponseModel getUserByEmail(String email) {
        return serviceClient.getUserByEmail(email);
    }

    @Override
    public UserResponseModel getUserById(String id) {
        return serviceClient.getUserById(id);
    }
}
