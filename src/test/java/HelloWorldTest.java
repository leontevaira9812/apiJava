import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.HashMap;
import java.util.Map;


public class HelloWorldTest {
    @Test
    public void testRestAssuredEx7() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect").andReturn();
        int statusCode = response.getStatusCode();
        int count = 0;
        while (statusCode != 200) {
            String returnUrl = response.getHeader("Location");
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(returnUrl).andReturn();
            statusCode = response.getStatusCode();
            count++;
            System.out.println("Редирект номер " + count + " = " + returnUrl);
        }
        System.out.println("Количество редиректов = " + count);
    }

    @Test
    public void testRestAssuredEx9() {
        String[] passwords = new String[]{"123456", "1234567", "12345678", "password", "qwerty", "abc123", "12345",
                "111111", "123123", "football", "Football", "monkey", "dragon", "123456789", "letmein", "iloveyou",
                "welcome", "master", "sunshine", "trustno1", "princess", "admin", "password1", "1234", "baseball",
                "passw0rd", "shadow", "654321", "1234567890", "login", "michael", "qwerty123", "solo", "qwertyuiop",
                "ashley", "mustang", "starwars", "superman", "qazwsx", "121212", "555555", "666666", "696969",
                "888888", "7777777", "adobe123", "1q2w3e4r", "photoshop", "1qaz2wsx", "bailey", "access", "flower",
                "lovely", "!@#$%^&*", "jesus", "hello", "charlie", "hottie", "freedom", "aa123456", "ninja", "azerty", "loveme",
                "whatever", "donald", "batman", "zaq1zaq1", "000000", "123qwe",
                "123456", "123456789", "qwerty", "12345678", "111111", "1234567890", "1234567", "password", "123123",
                "987654321", "qwertyuiop", "mynoob", "123321", "666666", "18atcskd2w", "7777777", "1q2w3e4r",
                "654321", "555555", "3rjs1la7qe", "google", "1q2w3e4r5t", "123qwe ", "zxcvbnm", "1q2w3e"};
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < passwords.length; i++) {
            data.put("login", "super_admin");
            data.put("password", passwords[i]);
            Response responseForGet = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework").andReturn();

            String responseCookies = responseForGet.getCookie("auth_cookie");
            Map<String, String> cookie = new HashMap<>();
            if (responseCookies != null) {
                cookie.put("auth_cookie", responseCookies);
            }

            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookie)
                    .when()
                    .post("https://playground.learnqa.ru/api/check_auth_cookie").andReturn();

            if (responseForCheck.print().equals("You are authorized")) {
                System.out.println("Ваш пароль = " + data.get("password"));
            }
        }
    }

    @Test
    public void testRestAssuredEx8() throws InterruptedException {
        JsonPath responseCreate = RestAssured
                .given()
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job").jsonPath();
        responseCreate.prettyPrint();
        String token = responseCreate.get("token");
        int seconds = responseCreate.get("seconds");

        JsonPath responseBefore = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job").jsonPath();
        responseBefore.prettyPrint();
        assert responseBefore.get("status").equals("Job is NOT ready");
        Thread.sleep(seconds * 1000);

        JsonPath responseAfter = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job").jsonPath();
        responseAfter.prettyPrint();
        assert responseAfter.get("status").equals("Job is ready");
        assert responseAfter.get("result") != null;

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Pete"})
    public void testHelloWithParameters(String name) {
        Map<String, String> query = new HashMap<>();
        if (name.length() > 0) {
            query.put("name", name);
        }
        JsonPath response = RestAssured
                .given()
                .queryParams(query)
                .get("https://playground.learnqa.ru/api/hello").jsonPath();
        String answer = response.get("answer");
        String expectedName = name.length() > 0 ? name : "someone";
        assertEquals("Hello, " + expectedName, answer, "The answer is not expected");
    }

    @Test
    public void testPositiveAuth() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        Map<String, String> cookies = responseLogin.getCookies();
        Headers headers = responseLogin.getHeaders();
        int userOnIdOnLogin = responseLogin.jsonPath().getInt("user_id");

        assertEquals(200, responseLogin.getStatusCode(), "Status code is not 200");
        assertTrue(cookies.containsKey("auth_sid"), "Response does not contains 'auth_sid");
        assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response does not contains 'x-csrf-token'");
        assertTrue(responseLogin.jsonPath().getInt("user_id") > 0, "User id should be greater than 0");

        JsonPath responseAuth = RestAssured
                .given()
                .header("x-csrf-token", responseLogin.getHeader("x-csrf-token"))
                .cookie("auth_sid", responseLogin.getCookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();
        int userIdonAuth = responseAuth.getInt("user_id");
        assertTrue(userIdonAuth > 0, "Unexpected userId " + userIdonAuth);
        assertEquals(userIdonAuth, userOnIdOnLogin, "UserId does not match");
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeTest(String condition) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        Map<String, String> cookies = responseLogin.getCookies();
        Headers headers = responseLogin.getHeaders();

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", responseLogin.getCookie("auth_sid"));
        } else if (condition.equals("headers")) {
            spec.cookie("x-csrf-token", responseLogin.getHeader("x-csrf-token"));
        } else {
            throw new IllegalArgumentException("Condition value is known :" + condition);
        }
        JsonPath responseAuth = spec.get().jsonPath();
        assertEquals(0, responseAuth.getInt("user_id"), "user id should be greater 0");

    }

    @Test
    public void ex10() {
        String text = "helloWorldText";
        assertTrue(text.length() > 15, "text is less than 15 symbols");
    }

    @Test
    public void ex11(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie").andReturn();
        Map<String,String> cookies = response.getCookies();
        String cookie_value = cookies.get("HomeWork");
        assertEquals("hw_value",cookie_value, "cookie not equals");
    }

    @Test
    public void ex12(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header").andReturn();
        Headers headers = response.getHeaders();
        assertTrue(headers.hasHeaderWithName("x-secret-homework-header"), "Response does not contains expected header");
        assertEquals("Some secret value",headers.getValue("x-secret-homework-header"),"values are not equal");
//
    }
}

