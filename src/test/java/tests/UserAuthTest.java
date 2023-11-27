package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;


@Epic("Authorisation cases")
@Feature("Authorisation")
public class UserAuthTest extends BaseTestcase {

    String cookie;
    String header;
    int userIdOnLogin;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseLogin, "auth_sid");
        this.header = this.getHeader(responseLogin, "x-csrf-token");
        this.userIdOnLogin = this.getIntFromJson(responseLogin, "user_id");

    }

    @Test
    @Description("This test successfully auth user by email and password")
    @DisplayName("Positive test")
    public void testPositiveAuth() {
        Response responseAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.header, this.cookie);
        Assertions.assertJsonByName(responseAuth, "user_id", this.userIdOnLogin);
    }

    @Description("This test checks auth without sending token or cookie")
    @DisplayName("Negative tests")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeTest(String condition) {
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie("https://playground.learnqa.ru/api/user/auth", this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("header")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken("https://playground.learnqa.ru/api/user/auth", this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is not known :" + condition);
        }
    }
}
