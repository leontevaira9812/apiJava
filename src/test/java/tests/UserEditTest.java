package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final UserHelper userHelper = new UserHelper();

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
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void editUserWithNotAuthoriseUser17_1() {
        //create
        Map<String, String> userData = DataGeneretor.getRegistrationData();
        Response responseCreate = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        System.out.println(responseCreate.asString());
        String userId = responseCreate.jsonPath().get("id");
        System.out.println(userId);

        //login
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));
        Response responseLogin = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", loginData);
        //System.out.println(responseLogin.getHeaders());

        //edit
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId, editData);
        System.out.println(responseEditUser.asString());

        //get
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"), this.getCookie(responseLogin, "auth_sid"));

        System.out.println(responseUserData.asString());
        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    public void editSecondUserWithLoginFirstUser17_2() {
        //создаем первого пользователя
        Map<String, String> userData = DataGeneretor.getRegistrationData();
        Response createResponseFirstUser = userHelper.createUser(userData);
        String userFirstId = createResponseFirstUser.jsonPath().get("id");

        //создаем второго пользователя
        Map<String, String> userSecondData = DataGeneretor.getRegistrationData();
        Response createResponseSecondUser = userHelper.createUser(userSecondData);
        String userSecondId = createResponseSecondUser.jsonPath().get("id");

        //логинимся с первым пользователем
        Response loginResponseFirstUser = userHelper.loginUser(userData);
        //изменяем данные второму пользователю, будучи залогинены первым пользователем


        userHelper.editFirstName(loginResponseFirstUser, userSecondId);
        Response responseGetFirstUser = userHelper.getUser(loginResponseFirstUser, userFirstId);
        responseGetFirstUser.print();

        //проверяем,что у первого пользователя данные не поменялись
        Assertions.assertJsonByName(responseGetFirstUser, "firstName", userData.get("firstName"));
        //логинимся вторым пользователем
        Response loginResponseSecondUser = userHelper.loginUser(userSecondData);
        //получаем второго пользователя
        Response responseGetUser2 = userHelper.getUser(loginResponseSecondUser, userSecondId);
        //проверяем,что у 2го пользователя не поменялось имя
        Assertions.assertJsonByName(responseGetUser2, "firstName", userSecondData.get("firstName"));
    }

    @Test()
    public void editEmailToIncorrect17_3() {
        //create
        Map<String, String> userData = DataGeneretor.getRegistrationData();
        Response responseFromCreate = userHelper.createUser(userData);
        String idUser = responseFromCreate.jsonPath().get("id");
        //login
        Response responseFromLogin = userHelper.loginUser(userData);

        Response responseFromEdit = userHelper.editUserEmail(responseFromLogin, idUser);
        Assertions.assertResponseEquals(responseFromEdit, "Invalid email format");
    }

    @Test
    public void changeFirstNametoSHort17_4(){
        //create
        Map<String, String> userData = DataGeneretor.getRegistrationData();
        Response responseFromCreate = userHelper.createUser(userData);
        String idUser = responseFromCreate.jsonPath().get("id");
        //login
        Response responseFromLogin = userHelper.loginUser(userData);

        Response responseFromEdit = userHelper.editFirstName(responseFromLogin, idUser,1);
        Assertions.assertJsonByName(responseFromEdit,"error","Too short value for field firstName");

    }

}
