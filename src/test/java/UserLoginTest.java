import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@DisplayName("Тесты логина пользователя")
public class UserLoginTest {

    User user;
    UserClient userClient;
    UserCredentials userCredentials;

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        userCredentials = new UserCredentials();
        userClient.createUser(user);
    }

    @After
    public void tearDown() {
        if (userClient.createUser(user).extract().statusCode() == 200) {
            String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void UserCorrectLoginTest() {
        ValidatableResponse response = userClient.loginUser(UserCredentials.from(user));

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");

        assertEquals("Отличный от ожидаемого код ответа", 200, statusCode);
        assertTrue("Значение должно быть true", success);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void UserRandomLoginTest() {
        user = User.getRandom();
        ValidatableResponse response = userClient.loginUser(UserCredentials.from(user));

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 401, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке",  "email or password are incorrect", message);
    }
}
