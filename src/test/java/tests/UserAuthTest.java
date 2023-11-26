package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestcase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;


public class UserAuthTest extends BaseTestcase {

    String cookie;
    String header;
    int userIdOnLogin;

    @BeforeEach
    public void loginUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password","1234");

        Response responseLogin = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
       this.cookie = this.getCookie(responseLogin, "auth_sid");
       this.header = this.getHeader(responseLogin,"x-csrf-token");
       this.userIdOnLogin = this.getIntFromJson(responseLogin,"user_id");

    }
    @Test
    public void testPositiveAuth(){
        Response responseAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid",this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth").andReturn();
        Assertions.assertJsonByName(responseAuth,"user_id",this.userIdOnLogin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie","headers"})
    public void testNegativeTest(String condition){
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        }else if(condition.equals("headers")){
            spec.cookie("x-csrf-token",this.header);
        }else {
            throw new IllegalArgumentException("Condition value is known :" +condition);
        }
        Response responseAuth = spec.get().andReturn();
        Assertions.assertJsonByName(responseAuth,"user_id",0);

    }
}
