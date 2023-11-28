package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestcase {

    protected String getHeader(Response Response, String name) {
        Headers headers = Response.getHeaders();
        assertTrue(headers.hasHeaderWithName(name), "Response doesn`t have header with " + name);
        return headers.getValue(name);
    }

    protected String getCookie(Response Response, String name) {
        Map<String, String> cookie = Response.getCookies();

        assertTrue(cookie.containsKey(name), "Response doesn`t have cookie with " + name);
        return cookie.get(name);
    }
    protected int getIntFromJson(Response Response, String name){
        Response.then().assertThat().body("$",hasKey(name));
        return Response.jsonPath().getInt(name);

    }
}