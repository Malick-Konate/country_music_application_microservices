package com.konate.music_application.ordersubdomain.domainClientLayer.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.ordersubdomain.Exceptions.HttpErrorInfo;
import com.konate.music_application.ordersubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.ordersubdomain.Exceptions.NotFoundException;
import com.konate.music_application.ordersubdomain.Exceptions.UserFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Component
@Slf4j
public class UserServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String USER_SERVICE_BASE_url;

    public UserServiceClient(RestTemplate restTemplate,
                             ObjectMapper mapper,
                             @Value("${app.user.host}") String userServiceHost,
                             @Value("${app.user.port}") String userServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
        this.USER_SERVICE_BASE_url = "http://" + userServiceHost + ":" + userServicePort + "/api/v1/users";
    }

    public UserModel getUserById(String userId) {

        try {
            String url = USER_SERVICE_BASE_url + "/" + userId;
            log.info("Calling User Service at {}", url);
            return restTemplate.getForObject(url, UserModel.class);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public List<UserModel> getAllUsers(){
        try {
            String url = USER_SERVICE_BASE_url;
            log.info("Calling User Service at {}", url);

            UserModel[] userArray = restTemplate.getForObject(url, UserModel[].class);
            return Arrays.asList(userArray);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public UserModel getUserByUsername(String username) {
        try {
            String url = USER_SERVICE_BASE_url + "/" + username;
            log.info("Calling User Service at {}", url);
            return restTemplate.getForObject(url, UserModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public UserModel getUserByEmail(String email) {
        try {
            String url = USER_SERVICE_BASE_url + "/email/" + email;
            log.info("Calling User Service at {}", url);
            return restTemplate.getForObject(url, UserModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public  UserModel createUser(UserModel requestModel) {
        try {
            String url = USER_SERVICE_BASE_url;
            log.info("Calling User Service at {}", url);
            return restTemplate.postForObject(url, requestModel, UserModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public UserModel updateUser(String username, UserModel requestModel) {
        try{
            String url = USER_SERVICE_BASE_url + "/" + username;
            log.info("Calling User Service at {}", url);
            restTemplate.put(url, requestModel, UserModel.class);
            return getUserByUsername(username);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public void deleteUser(String username){
        try{
            String url = USER_SERVICE_BASE_url + "/" + username;
            log.info("Calling User Service at {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
//    public void changePassWord(String username, String password){
//        try {
//            String url = USER_SERVICE_BASE_url + username + "/password";
//            log.info("Calling User Service at {}", url);
//            restTemplate.patchForObject(url, password, UserResponseModel.class);
//        } catch (HttpClientErrorException ex) {
//            throw handleHttpClientException(ex);
//        }
//    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == CONFLICT) {
            return new UserFound(getErrorMessage(ex));
        }

        log.warn("Got an unexpected HTTP error from User Service: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }


    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            // Assuming the error response body can be mapped to HttpErrorInfo
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            // Fallback if the error body cannot be parsed
            return ioex.getMessage();
        }
    }

}
