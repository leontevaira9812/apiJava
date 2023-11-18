import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
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
}
