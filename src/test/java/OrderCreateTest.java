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

@DisplayName("Тесты создание заказа")
public class OrderCreateTest {

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
        if (userClient.loginUser(UserCredentials.from(user)).extract().statusCode() == 200) {
            String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void OrderWithLoginTest() {
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        order = Order.getRealIngredients();

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String number = response.extract().path("number");

        assertEquals("Отличный от ожидаемого код ответа", 200, statusCode);
        assertTrue("Значение должно быть true", success);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void OrderWithoutLoginTest() {
        // Ожидается поведение схожее с редактированием профиля без авторизации, в документации ничего не сказано
        order = Order.getRealIngredients();

        ValidatableResponse response = orderClient.createOrder("", order);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 403, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "models.User with such email already exists", message);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void OrderWithoutIngredientTest() {
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        order = new Order();

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 400, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Создание заказа с частично неправильными ингредиентами")
    public void OrderWithSomeWrongIngredientTest() {
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        order = Order.getSomeUnrealIngredients();

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Отличный от ожидаемого код ответа", 400, statusCode);
        assertFalse("Значение должно быть false", success);
        assertEquals("Неверное сообщение об ошибке", "One or more ids provided are incorrect", message);
    }

    @Test
    @DisplayName("Создание заказа со всеми неправильными ингредиентами (невалидный хэш)")
    public void OrderWithAllWrongIngredientTest() {
        String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));

        order = Order.getAllUnrealIngredients();

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        int statusCode = response.extract().statusCode();

        assertEquals("Отличный от ожидаемого код ответа", 500, statusCode);
    }
}
