import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest {

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
       this.cookie = responseLogin.getCookie("auth_sid");
       this.header = responseLogin.getHeader("x-csrf-token");
       this.userIdOnLogin = responseLogin.jsonPath().getInt("user_id");

    }
    @Test
    public void testPositiveAuth(){
        JsonPath responseAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid",this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();
        int userIdonAuth = responseAuth.getInt("user_id");
        assertTrue(userIdonAuth > 0 , "Unexpected userId " + userIdonAuth);
        assertEquals(userIdonAuth,this.userIdOnLogin, "UserId does not match");
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
        JsonPath responseAuth = spec.get().jsonPath();
        assertEquals(0,responseAuth.getInt("user_id"), "user id should be greater 0");

    }
}
