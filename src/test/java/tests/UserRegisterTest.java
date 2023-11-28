package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGeneretor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.HashMap;
import java.util.Map;



public class UserRegisterTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData = DataGeneretor.getRegistrationData(authData);

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/").andReturn();

        Assertions.assertResponseEquals(response, "Users with email '" + email + "' already exists");
        Assertions.assertCodeEquals(response, 400);
    }

    @Test
    public void testCreateUser() {
        Map<String, String> authData = DataGeneretor.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/").andReturn();

        Assertions.assertCodeEquals(response, 200);
        Assertions.assertJsonHasField(response, "id");
    }

    @Test
    public void testCreateUserWithIncorrectEmail() {
        String expectedResult = "Invalid email format";
        Map<String,String> authData = new HashMap<>();
        authData.put("email","testgmail.com");
        authData = DataGeneretor.getRegistrationData(authData);

        Response response = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",authData);
        Assertions.assertResponseEquals(response, expectedResult);

    }

    public static Object[][] testData() {
        return new Object[][]{
                {null,"firstName", "lastName", "email","password"},
                {"username",null, "lastName", "email","password"},
                {"username", "firstName", null, "email","password"},
                {"username", "firstName", "lastName",null, "password"},
                {"username", "firstName", "lastName", "email", null}};
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void testCreateUserWithoutEachField(String userName, String firstName, String lastName, String email, String password) {
        String expectedResult = "";
        Map<String,String> userData = new HashMap<>();
        userData.put("username",userName);
        userData.put("firstName",firstName);
        userData.put("lastName",lastName);
        userData.put("email",email);
        userData.put("password",password);
        for (Map.Entry entry : userData.entrySet()) {
            if(entry.getValue()==null){
                expectedResult = "The following required params are missed: " + entry.getKey();
            }
        }
        Response response = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseEquals(response, expectedResult);
    }

    @Test
    public void testCreateUserWithShortName(){
        Map<String,String> userData = new HashMap<>();
        userData.put("firstName",DataGeneretor.getRandomString(1));
        userData = DataGeneretor.getRegistrationData(userData);
        Response response = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseEquals(response,"The value of 'firstName' field is too short");
    }

    @Test
    public void testCreateUserWithLongName(){
        Map<String,String> userData = new HashMap<>();
        userData.put("firstName",DataGeneretor.getRandomString(251));
        userData = DataGeneretor.getRegistrationData(userData);
        Response response = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseEquals(response,"The value of 'firstName' field is too long");
    }

}
