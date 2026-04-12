package com.konate.music_application.usersubdomain.presentation;

import com.konate.music_application.usersubdomain.PresentationLayer.UserRequestModel;
import com.konate.music_application.usersubdomain.PresentationLayer.UserResponseModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String BASE_URI = "/api/v1/users";

    // Test Data based on your SQL insert
    private final String VALID_USER_ID = "user-001";
    private final String VALID_USER_NAME = "malick";
    private final String VALID_EMAIL = "malick@email.com";
    private final String INVALID_USER_NAME = "non-existent-user";
    private final String INVALID_USER_ID = "non-existent-id";

    @Test
    void whenUserExist_thenReturnAllUsers() {
        webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponseModel.class)
                .value(users -> {
                    assertNotNull(users);
                    assertFalse(users.isEmpty());
                    assertEquals(10, users.size()); // Based on your 10 SQL inserts
                });
    }

    @Test
    void whenValidUsername_thenReturnUser() {
        webTestClient.get()
                .uri(BASE_URI + "/" + VALID_USER_NAME)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    assertNotNull(user);
                    assertEquals(VALID_USER_NAME, user.getUsername());
                    assertEquals(VALID_USER_ID, user.getUserId());
                });
    }

    @Test
    void whenValidUserId_thenReturnUser() {
        webTestClient.get()
                .uri(BASE_URI + "/id/" + VALID_USER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    assertNotNull(user);
                    assertEquals(VALID_USER_ID, user.getUserId());
                });
    }

    @Test
    void whenValidEmail_thenReturnUser() {
        webTestClient.get()
                .uri(BASE_URI + "/email/" + VALID_EMAIL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    assertNotNull(user);
                    assertEquals(VALID_EMAIL, user.getEmail());
                });
    }

    @Test
    void whenValidRequest_thenCreateUser() {
        UserRequestModel request = buildSampleUser();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseModel.class)
                .value(created -> {
                    assertNotNull(created);
                    assertEquals(request.getUsername(), created.getUsername());
                    assertEquals(request.getEmail(), created.getEmail());
                });
    }

    @Test
    void whenUpdateValidUser_thenReturnUpdatedUser() {
        UserRequestModel updatedRequest = buildSampleUser();
        updatedRequest.setFullname("Updated Name");

        webTestClient.put()
                .uri(BASE_URI + "/" + VALID_USER_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updatedRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(updated -> {
                    assertNotNull(updated);
                    assertEquals("Updated Name", updated.getFullname());
                });
    }

    @Test
    void whenUserDelete_thenReturnNoContent() {
        webTestClient.delete()
                .uri(BASE_URI + "/" + VALID_USER_NAME)
                .exchange()
                .expectStatus().isNoContent();

        // Verify it is gone
        webTestClient.get()
                .uri(BASE_URI + "/" + VALID_USER_NAME)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenChangePassword_thenReturnNoContent() {
        UserRequestModel passwordRequest = new UserRequestModel();
        passwordRequest.setPassword("new_secure_password");

        webTestClient.patch()
                .uri(BASE_URI + "/" + VALID_USER_NAME + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(passwordRequest)
                .exchange()
                .expectStatus().isNoContent();
    }

    // --- Failure Scenarios ---

    @Test
    void whenUsernameNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URI + "/" + INVALID_USER_NAME)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUserIdNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URI + "/id/" + INVALID_USER_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateInvalidUser_thenReturnNotFound() {
        UserRequestModel request = buildSampleUser();
        webTestClient.put()
                .uri(BASE_URI + "/" + INVALID_USER_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateUserWithEmptyBody_thenBadRequest() {
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("") // invalid
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void whenCreateDuplicateUser_thenConflictOrError() {
        UserRequestModel request = buildSampleUser();
        request.setUsername(VALID_USER_NAME);

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError(); // usually 409 or 422
    }

    @Test
    void whenEmailNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URI + "/email/nonexistent@email.com")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateWithInvalidBody_thenBadRequest() {
        webTestClient.put()
                .uri(BASE_URI + "/" + VALID_USER_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("") // invalid JSON
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void whenDeleteInvalidUser_thenReturnNotFound() {
        webTestClient.delete()
                .uri(BASE_URI + "/non-existent-user")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenChangePasswordUserNotFound_thenNotFound() {
        UserRequestModel request = new UserRequestModel();
        request.setPassword("newPassword");

        webTestClient.patch()
                .uri(BASE_URI + "/non-existent-user/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenChangePasswordInvalidBody_thenBadRequest() {
        webTestClient.patch()
                .uri(BASE_URI + "/" + VALID_USER_NAME + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("") // invalid
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void whenUsernameEmpty_thenBadRequest() {
        webTestClient.get()
                .uri(BASE_URI + "/ ")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void whenUserIdInvalid_thenBadRequest() {
        webTestClient.get()
                .uri(BASE_URI + "/id/ ")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void whenCreateUserNull_thenBadRequest() {
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}") // missing required fields
                .exchange()
                .expectStatus().is4xxClientError();
    }
    private UserRequestModel buildSampleUser() {
        return UserRequestModel.builder()
                .username("newuser")
                .email("newuser@email.com")
                .password("password123")
                .fullname("New User")
                .age(25)
                .country("Canada")
                .build();
    }
}