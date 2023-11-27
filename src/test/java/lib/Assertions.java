package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Assertions {

    public static void assertJsonByName(Response Response, String name, int expectedValue){
        Response.then().assertThat().body("$",hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue,value, "JSON value is not equal to expected value");

    }

    public static void assertJsonByName(Response Response, String name, String expectedValue){
        Response.then().assertThat().body("$",hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue,value, "JSON value is not equal to expected value");

    }

    public static void assertEmailEquals(Response Response,String expectedText){
       assertEquals(expectedText,Response.asString(),"Response text is not as expected");
    }

    public static void assertCodeEquals(Response Response,int expectedCode){
        assertEquals(expectedCode,Response.getStatusCode(),"Response code is not as expected");
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName) {
        Response.then().body("$",hasKey(expectedFieldName));
    }

    public static void assertJsonHasFields(Response Response, String[] expectedFieldsName) {
        for (String field : expectedFieldsName){
            assertJsonHasField(Response,field);
        }
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName){
        Response.then().body("$",not(unexpectedFieldName));
    }
}
