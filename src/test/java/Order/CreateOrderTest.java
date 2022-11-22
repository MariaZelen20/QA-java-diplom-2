package Order;

import User.User;
import User.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CreateOrderTest {

    private OrderApi orderApi;
    private UserApi userApi;
    private User user;
    private String accessToken;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        userApi = new UserApi();
        user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        ValidatableResponse ingredientsResponse = OrderApi.getAllIngredientsData();
        firstIngredient = ingredientsResponse.extract().path("data[0]._id");
    }

    @After
    public void tearDown() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("C авторизацией, с ингредиентами")
    public void creatingOrderWithAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));

        ValidatableResponse createOrderResponse = OrderApi.createOrderWithAuth(accessToken, order);
        int statusCode = createOrderResponse.extract().statusCode();
        String name = createOrderResponse.extract().path("name");
        boolean isOrderCreated = createOrderResponse.extract().path("success");

        assertEquals("Статус код не совпадает", 200, statusCode);
        assertNotNull("Имя пустое", name);
        assertTrue("Заказ не сделан", isOrderCreated);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Без авторизации, с ингредиентами")
    public void creatingOrderWithoutAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));

        ValidatableResponse createOrderResponse = OrderApi.createOrderWithoutAuth(order);
        int statusCode = createOrderResponse.extract().statusCode();
        String name = createOrderResponse.extract().path("name");
        boolean isOrderCreated = createOrderResponse.extract().path("success");

        assertEquals("Статус код не совпадает", 200, statusCode);
        assertNotNull("Имя пустое", name);
        assertTrue("Заказ не сделан", isOrderCreated);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Без ингредиентов")
    public void creatingOrderWithoutIngredient() {
        Order order = new Order();

        ValidatableResponse createOrderResponse = OrderApi.createOrderWithoutAuth(order);
        int statusCode = createOrderResponse.extract().statusCode();
        String message = createOrderResponse.extract().path("message");
        boolean isOrderCreated = createOrderResponse.extract().path("success");

        assertEquals("Статус код не совпадает", 400, statusCode);
        assertFalse("Заказ сделан", isOrderCreated);
        assertEquals("Сообщение об ошибке не совпадает", "Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("С неверным хешем ингредиентов")
    public void creatingOrderWithWrongHashes() {
        String wrongHashes = Order.getSomeRandomIngredientHash();
        Order order = new Order();
        order.setIngredients(Arrays.asList(wrongHashes));

        ValidatableResponse createOrderResponse = OrderApi.createOrderWithoutAuth(order);
        int statusCode = createOrderResponse.extract().statusCode();

        assertEquals("Статус код не совпадает", 500, statusCode);
    }
}
