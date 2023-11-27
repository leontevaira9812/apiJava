package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGeneretor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserRegisterTest extends BaseTestcase {
    @Test
    public void testWithExistingEmail(){
        String email = "vinkotov@example.com";
        Map<String,String> authData = new HashMap<>();
        authData.put("email",email);
        authData = DataGeneretor.getRegistrationData(authData);

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/").andReturn();

        Assertions.assertEmailEquals(response,"Users with email '"+ email + "' already exists");
        Assertions.assertCodeEquals(response,400);
    }
    @Test
    public void testCreateUser(){
        Map<String,String> authData = DataGeneretor.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/").andReturn();

        Assertions.assertCodeEquals(response,200);
        Assertions.assertJsonHasField(response,"id");
    }

}
