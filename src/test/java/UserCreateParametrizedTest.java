import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
@DisplayName("Параметризованные тесты создания пользователя с неверными данными")
public class UserCreateParametrizedTest {

    UserClient userClient;
    User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    public UserCreateParametrizedTest(User user) {
        this.user = user;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0}")
    public static Object[][] getTestData() {
        return new Object[][] {
                {User.createUserWithoutEmail()},
                {User.createUserWithoutName()},
                {User.createUserWithoutPassword()}
        };
    }

    @Test
    @DisplayName("Создание пользователя с незаполненными полями")
    public void UserCreateWithInvalidCredentialsTest() {
        ValidatableResponse response = userClient.createUser(user);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 403, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "Email, password and name are required fields", message);
    }
}
