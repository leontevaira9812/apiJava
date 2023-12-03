package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGeneretor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void negativeTest() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);


        Response responseDelete = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + responseLogin.jsonPath().get("user_id"),
                        this.getHeader(responseLogin, "x-csrf-token"), this.getCookie(responseLogin, "auth_sid"));
        Assertions.assertResponseEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    public void positiveTest() {
        //create
        Map<String, String> userData = DataGeneretor.getRegistrationData();

        Response responseCreate = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String id = responseCreate.jsonPath().get("id");
        responseCreate.prettyPrint();

        //login
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));
        Response responseLogin = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", loginData);


        //delete
        Response responseDelete = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + id,
                        this.getHeader(responseLogin, "x-csrf-token"), this.getCookie(responseLogin, "auth_sid"));

        //get
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + id);

        Assertions.assertResponseEquals(responseUserData, "User not found");
    }

    @Test
    public void negativeDeleteAnotherUser(){
        //create
        Map<String, String> userData = DataGeneretor.getRegistrationData();
        Response responseCreate = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        //create for Delete
        Map<String, String> userDataForDelete = DataGeneretor.getRegistrationData();
        Response responseCreateForDeleteUser = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userDataForDelete);
        String id = responseCreateForDeleteUser.jsonPath().get("id");


        //login
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));
        Response responseLogin = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", loginData);


        //delete
        Response responseDelete = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + id,
                        this.getHeader(responseLogin, "x-csrf-token"), this.getCookie(responseLogin, "auth_sid"));

        //get
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + id);

        Assertions.assertJsonByName(responseUserData,"username",userDataForDelete.get("username"));
    }
}

