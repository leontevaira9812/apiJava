package lib;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class UserHelper extends BaseTestcase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Step
    public Response createUser(Map<String, String> userData) {
        // Map<String,String> userData =DataGeneretor.getRegistrationData();
        Response responseCreate = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        System.out.println(responseCreate.asString());
        return responseCreate;
    }

    @Step
    public Response loginUser(Map<String, String> userData) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));
        return apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", loginData);
    }

    @Step
    public Response editFirstName(Response response, String id) {
      return editFirstName (response, id, 10);

//        String newName = "changedName";
//        Map<String, String> editData = new HashMap<>();
//        editData.put("firstName", newName);
//        return apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + id,
//                editData, this.getHeader(response, "x-csrf-token"), this.getCookie(response, "auth_sid"));
        // System.out.println("responseEditUser " + responseEditUser.asString());
    }


    @Step
    public Response editFirstName(Response response, String id, int lenght) {
        String newName = DataGeneretor.getRandomString(lenght);
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        return apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + id,
                editData, this.getHeader(response, "x-csrf-token"), this.getCookie(response, "auth_sid"));
    }

    @Step
    public Response editUserEmail(Response response, String id) {
        String newEmail = "testinggmail.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);
        return apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + id,
                editData, this.getHeader(response, "x-csrf-token"), this.getCookie(response, "auth_sid"));
        // System.out.println("responseEditUser " + responseEditUser.asString());
    }

    @Step
    public Response getUser(Response responseFromLogin, String id) {
        return apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + id,
                this.getHeader(responseFromLogin, "x-csrf-token"), this.getCookie(responseFromLogin, "auth_sid"));
    }


}
