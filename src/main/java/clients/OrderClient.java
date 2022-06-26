package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    @Step("Получить заказы конкретного пользователя")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", token)
                .get("/orders")
                .then();
    }

    @Step("Создать заказ")
    public ValidatableResponse createOrder(String token, Order order) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", token)
                .body(order)
                .post("/orders")
                .then();
    }
}
