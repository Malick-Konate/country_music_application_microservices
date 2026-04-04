package com.konate.music_application.apigateway.PresentationLayer.User;

import com.konate.music_application.apigateway.BusinessLayer.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<UserResponseModel>> getAllUsers() {
        log.info("Getting all users");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<UserResponseModel> createUser(@RequestBody UserRequestModel user) {
        log.info("Creating user: {}", user);
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }

    @GetMapping(
            value = "/id/{userId}",
            produces = "application/json"

    )
    public ResponseEntity<UserResponseModel> getUser(@PathVariable String userId) {
        log.info("Getting user by ID: {}", userId);
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{username}",
            produces = "application/json"
    )
    public ResponseEntity<UserResponseModel> getUserByUsername(@PathVariable String username) {
        log.info("Getting user by username: {}", username);
        return new ResponseEntity<>(userService.getUserByUsername(username), HttpStatus.OK);
    }

    @PutMapping(
            value = "/{username}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<UserResponseModel> updateUser(@PathVariable String username, @RequestBody UserRequestModel user) {
        log.info("Updating user by username: {}", username);
        return new ResponseEntity<>(userService.updateUser(username, user), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/{username}",
            produces = "application/json"
    )
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        log.info("Deleting user by username: {}", username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
