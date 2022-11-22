package Order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static Config.Config.getDefaultRequestSpec;
import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String ORDER_PATH = "api/orders/";

    @Step("Создаем пользователя с авторизацией")
    public static ValidatableResponse createOrderWithAuth(String accessToken, Order order) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Создаем пользователя без авторизации")
    public static ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получаем данные об ингредиентах")
    public static ValidatableResponse getAllIngredientsData() {
        return given()
                .spec(getDefaultRequestSpec())
                .when()
                .get("/api/ingredients")
                .then();
    }

    @Step("Получаем заказы конкретного пользователя")
    public ValidatableResponse getOrderDataForUserWithAuth(String accessToken) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Получаем заказы без токена")
    public ValidatableResponse getOrderDataWithoutAuth() {
        return given()
                .spec(getDefaultRequestSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Десериализуем заказы конкретного пользователя")
    public OrderData orderData (String accessToken) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .body().as(OrderData.class);
    }
}
