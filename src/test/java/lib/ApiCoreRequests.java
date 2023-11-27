package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("make GET request with token and auth cookie")
    @Test
    public Response makeGetRequest(String url,String token,String cookie){
       return given()
               .filter(new AllureRestAssured())
               .header(new Header("x-csrf-token", token))
               .cookie("auth_sid",cookie)
               .get(url)
               .andReturn();
    }
    @Step("make GET request with auth cookie only")
    @Test
    public Response makeGetRequestWithCookie(String url,String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }
    @Step("make GET request with token ")
    @Test
    public Response makeGetRequestWithToken(String url,String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }
    @Step("make POST request with token and auth cookie")
    @Test
    public Response makePostRequest(String url, Map<String,String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }
}
