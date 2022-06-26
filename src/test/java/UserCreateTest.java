import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

@DisplayName("Тесты создания пользователя")
public class UserCreateTest {

    User user;
    UserClient userClient;
    UserCredentials userCredentials;

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        userCredentials = new UserCredentials();
    }

    @After
    public void tearDown() {
        if (userClient.createUser(user).extract().statusCode() == 200) {
            String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Позитивный тест регистрации пользователя")
    public void UserCorrectCreateTest() {
        ValidatableResponse response = userClient.createUser(user);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");

        Map userDetails = response.extract().path("user");
        String expectedEmail = String.valueOf(userDetails.get("email"));
        String actualEmail = user.getEmail().toLowerCase();
        String expectedName = String.valueOf(userDetails.get("name"));
        String actualName = user.getName();

        assertEquals("Отличный от ожидаемого код ответа", 200, statusCode);
        assertTrue("Значение должно быть true", success);
        assertEquals("Значения мэйла должны совпадать", expectedEmail, actualEmail);
        assertEquals("Значения имени должны совпадать", expectedName, actualName);
    }

    @Test
    @DisplayName("Создать уже зарегистрированного пользователя")
    public void UserExistedCreateTest() {
        ValidatableResponse firstCreateResponse = userClient.createUser(user);

        int firstCreateStatusCode = firstCreateResponse.extract().statusCode();

        ValidatableResponse secondCreateResponse = userClient.createUser(user);
        int secondCreateStatusCode = secondCreateResponse.extract().statusCode();
        boolean success = secondCreateResponse.extract().path("success");
        String message = secondCreateResponse.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 200, firstCreateStatusCode);

        assertEquals("Отличный от ожидаемого код ответа", 403, secondCreateStatusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "User already exists", message);
    }
}
