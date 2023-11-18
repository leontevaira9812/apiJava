import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {
    @Test
    public void testRestAssured() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect").andReturn();
        int statusCode = response.getStatusCode();
        int count=0;
        while (statusCode != 200) {
            String returnUrl = response.getHeader("Location");
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(returnUrl).andReturn();
            statusCode = response.getStatusCode();
            count ++;
            System.out.println("Редирект номер " + count + " = " + returnUrl);
        }
        System.out.println("Количество редиректов = " + count);
    }
}
