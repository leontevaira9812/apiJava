package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestcase {
    @Test
    public void testGetUserDataNotAuth(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/user/2").andReturn();

        Assertions.assertJsonHasField(response,"username");
        Assertions.assertJsonHasNotField(response,"firstName");
        Assertions.assertJsonHasNotField(response,"lastName");
        Assertions.assertJsonHasNotField(response,"email");
    }
    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password","1234");

        Response responseLogin = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        String cookie = this.getCookie(responseLogin, "auth_sid");
        String header = this.getHeader(responseLogin,"x-csrf-token");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",header)
                .cookie("auth_sid",cookie)
                .get("https://playground.learnqa.ru/api/user/2").andReturn();

        String [] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }
}
