package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGeneretor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestcase {
    @Test
    public void testEditUser() {
        //generate user
        Map<String, String> userData = DataGeneretor.getRegistrationData();

        JsonPath responseCreate = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        responseCreate.prettyPrint();
        String userId = responseCreate.getString("id");
        System.out.println(userId);
        String id1 = responseCreate.get("id");
        System.out.println(id1);

        //login

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(loginData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        responseGetAuth.print();

        //edit
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEdit = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId).andReturn();

        //get

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        System.out.println(responseUserData.asString());
        Assertions.assertJsonByName(responseUserData,"firstName",newName);


    }
}
