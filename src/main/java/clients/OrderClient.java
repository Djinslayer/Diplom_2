package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    private static final String ORDERS_PATH = "/orders";

    @Step("Получить заказы конкретного пользователя")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", token)
                .get(ORDERS_PATH)
                .then();
    }

    @Step("Создать заказ")
    public ValidatableResponse createOrder(String token, Order order) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", token)
                .body(order)
                .post(ORDERS_PATH)
                .then();
    }
}
