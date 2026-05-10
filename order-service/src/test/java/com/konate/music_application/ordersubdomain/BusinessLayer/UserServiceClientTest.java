package com.konate.music_application.ordersubdomain.BusinessLayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.ordersubdomain.Exceptions.HttpErrorInfo;
import com.konate.music_application.ordersubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.ordersubdomain.Exceptions.NotFoundException;
import com.konate.music_application.ordersubdomain.Exceptions.UserFound;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    private UserServiceClient client;

    private static final String BASE_URL = "http://localhost:7002/api/v1/users";

    @BeforeEach
    void setUp() {
        client = new UserServiceClient(restTemplate, objectMapper, "localhost", "7002");
    }

    // =========================================================================
    // getUserById
    // =========================================================================

    @Test
    void getUserById_WhenUserExists_ReturnsUser() {
        UserModel expected = buildUser("user-001");
        when(restTemplate.getForObject(BASE_URL + "/id/user-001", UserModel.class))
                .thenReturn(expected);

        UserModel result = client.getUserById("user-001");

        assertNotNull(result);
        assertEquals("user-001", result.getUserId());
    }

//    @Test
//    void getUserById_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "User not found");
//        when(restTemplate.getForObject(contains("/id/"), eq(UserModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getUserById("bad-id"));
//    }
//
//    @Test
//    void getUserById_WhenUnprocessableEntity_ThrowsInvalidInputException() throws IOException {
//        stubHttpError(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input");
//        when(restTemplate.getForObject(contains("/id/"), eq(UserModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.UNPROCESSABLE_ENTITY));
//
//        assertThrows(InvalidInputException.class, () -> client.getUserById("user-001"));
//    }

    // =========================================================================
    // getAllUsers
    // =========================================================================

    @Test
    void getAllUsers_WhenUsersExist_ReturnsList() {
        UserModel[] users = { buildUser("user-001"), buildUser("user-002") };
        when(restTemplate.getForObject(BASE_URL, UserModel[].class)).thenReturn(users);

        List<UserModel> result = client.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

//    @Test
//    void getAllUsers_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "not found");
//        when(restTemplate.getForObject(eq(BASE_URL), eq(UserModel[].class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getAllUsers());
//    }

    // =========================================================================
    // getUserByEmail
    // =========================================================================

    @Test
    void getUserByEmail_WhenUserExists_ReturnsUser() {
        UserModel expected = buildUser("user-001");
        when(restTemplate.getForObject(BASE_URL + "/email/test@email.com", UserModel.class))
                .thenReturn(expected);

        UserModel result = client.getUserByEmail("test@email.com");

        assertNotNull(result);
    }

//    @Test
//    void getUserByEmail_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "not found");
//        when(restTemplate.getForObject(contains("/email/"), eq(UserModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getUserByEmail("ghost@email.com"));
//    }

    // =========================================================================
    // getUserByUsername
    // =========================================================================

    @Test
    void getUserByUsername_WhenUserExists_ReturnsUser() {
        UserModel expected = buildUser("user-001");
        when(restTemplate.getForObject(BASE_URL + "/malick", UserModel.class)).thenReturn(expected);

        UserModel result = client.getUserByUsername("malick");

        assertNotNull(result);
    }

//    @Test
//    void getUserByUsername_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "not found");
//        when(restTemplate.getForObject(contains("ghost"), eq(UserModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getUserByUsername("ghost"));
//    }

    // =========================================================================
    // createUser
    // =========================================================================

    @Test
    void createUser_WhenValid_ReturnsCreatedUser() {
        UserModel user = buildUser("user-001");
        when(restTemplate.postForObject(BASE_URL, user, UserModel.class)).thenReturn(user);

        UserModel result = client.createUser(user);

        assertNotNull(result);
        verify(restTemplate).postForObject(BASE_URL, user, UserModel.class);
    }

//    @Test
//    void createUser_WhenConflict_ThrowsUserFound() throws IOException {
//        stubHttpError(HttpStatus.CONFLICT, "User already exists");
//        when(restTemplate.postForObject(eq(BASE_URL), any(), eq(UserModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.CONFLICT));
//
//        assertThrows(UserFound.class, () -> client.createUser(buildUser("user-001")));
//    }
//
//    @Test
//    void createUser_WhenUnprocessableEntity_ThrowsInvalidInputException() throws IOException {
//        stubHttpError(HttpStatus.UNPROCESSABLE_ENTITY, "invalid input");
//        when(restTemplate.postForObject(eq(BASE_URL), any(), eq(UserModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.UNPROCESSABLE_ENTITY));
//
//        assertThrows(InvalidInputException.class, () -> client.createUser(buildUser("user-001")));
//    }

    // =========================================================================
    // updateUser
    // =========================================================================

    @Test
    void updateUser_WhenValid_ReturnsUpdatedUser() {
        UserModel user = buildUser("user-001");
        // updateUser calls put() then getUserByUsername()
        doNothing().when(restTemplate).put(anyString(), any(), eq(UserModel.class));
        when(restTemplate.getForObject(BASE_URL + "/malick", UserModel.class)).thenReturn(user);

        UserModel result = client.updateUser("malick", user);

        assertNotNull(result);
        verify(restTemplate).put(BASE_URL + "/malick", user, UserModel.class);
    }

    @Test
    void updateUser_WhenNotFound_ThrowsNotFoundException() throws IOException {
        stubHttpError(HttpStatus.NOT_FOUND, "not found");
        doThrow(makeHttpError(HttpStatus.NOT_FOUND))
                .when(restTemplate).put(anyString(), any(), eq(UserModel.class));

        assertThrows(NotFoundException.class,
                () -> client.updateUser("ghost", buildUser("user-001")));
    }

    // =========================================================================
    // deleteUser
    // =========================================================================

    @Test
    void deleteUser_WhenValid_DeletesSuccessfully() {
        doNothing().when(restTemplate).delete(BASE_URL + "/malick");

        assertDoesNotThrow(() -> client.deleteUser("malick"));
        verify(restTemplate).delete(BASE_URL + "/malick");
    }

    @Test
    void deleteUser_WhenNotFound_ThrowsNotFoundException() throws IOException {
        stubHttpError(HttpStatus.NOT_FOUND, "not found");
        doThrow(makeHttpError(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

        assertThrows(NotFoundException.class, () -> client.deleteUser("ghost"));
    }

    // =========================================================================
    // Unknown HTTP error — rethrows as-is
    // =========================================================================

    @Test
    void getUserById_WhenUnknownHttpError_RethrowsOriginalException() {
        HttpClientErrorException serverError = makeHttpError(HttpStatus.TOO_MANY_REQUESTS);
        when(restTemplate.getForObject(contains("/id/"), eq(UserModel.class)))
                .thenThrow(serverError);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class,
                () -> client.getUserById("user-001"));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, thrown.getStatusCode());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private UserModel buildUser(String userId) {
        return UserModel.builder()
                .userId(userId).fullname("Malick Konate")
                .email("malick@email.com").country("Canada").build();
    }

    /** Stubs ObjectMapper so getErrorMessage() returns a parseable message. */
    private void stubHttpError(HttpStatus status, String message) throws IOException {
        HttpErrorInfo errorInfo = mock(HttpErrorInfo.class);
        when(errorInfo.getMessage()).thenReturn(message);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);
    }

    private HttpClientErrorException makeHttpError(HttpStatus status) {
        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getStatusCode()).thenReturn(status);
        when(ex.getResponseBodyAsString()).thenReturn("{\"message\":\"error\"}");
        return ex;
    }
}