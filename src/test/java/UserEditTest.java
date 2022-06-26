import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@DisplayName("Тесты изменение данных пользователя")
public class UserEditTest {

    User user, editUser;
    UserClient userClient;
    UserCredentials userCredentials;

    @Before
    public void setUp() {
        user = User.getRandom();
        editUser = User.getRandom();
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
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void UserCorrectEditTest() {
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        ValidatableResponse response = userClient.editUser(accessToken, editUser);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");

        assertEquals("Отличный от ожидаемого код ответа", 200, statusCode);
        assertTrue("Значение должно быть true", success);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void UserWithoutAuthorizationEditTest() {
        ValidatableResponse response = userClient.editUser("", editUser);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 401, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "You should be authorised", message);
    }

    @Test
    @DisplayName("Изменение данных пользователя не меняя почту (проверка ошибки 403: models.User with such email already exists")
    public void UserEditWithSameEmailTest() {
        //Исходя из документации должно работать так как реализовано в тесте
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        User userWithSameEmail = new User(user.getEmail(), editUser.getPassword(), editUser.getName());

        ValidatableResponse response = userClient.editUser(accessToken, userWithSameEmail);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 403, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "models.User with such email already exists", message);
    }
}
