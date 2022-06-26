import clients.OrderClient;
import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.Order;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@DisplayName("Тесты получение заказов")
public class OrderGetTest {

    User user;
    UserClient userClient;
    UserCredentials userCredentials;
    OrderClient orderClient;
    Order order;

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        userCredentials = new UserCredentials();
        orderClient = new OrderClient();
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
    @DisplayName("Получение заказов пользователя (Пользователь авторизован)")
    public void getOrderListWithAuthUserTest() {
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        ValidatableResponse response = orderClient.getUserOrders(accessToken);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");

        assertEquals("Отличный от ожидаемого код ответа", 200, statusCode);
        assertTrue("Значение должно быть true", success);
    }

    @Test
    @DisplayName("Получение заказов пользователя (Пользователь авторизован)")
    public void getOrderListWithoutAuthUserTest() {
        ValidatableResponse response = orderClient.getUserOrders("");

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 401, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке",  "You should be authorised", message);
    }
}
