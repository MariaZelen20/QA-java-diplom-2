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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GetOrdersTest {
    private OrderApi orderClient;
    private User user;
    private UserApi userApi;
    private String accessToken;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderApi();
        userApi = new UserApi();
        user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        ValidatableResponse ingredientsResponse = orderClient.getAllIngredientsData();
        firstIngredient = ingredientsResponse.extract().path("data[0]._id");
    }

    @After
    public void tearDown() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Авторизованный пользователь")
    public void getOrderForUserWithAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));
        orderClient.createOrderWithAuth(accessToken, order);
        String[] expected = {firstIngredient};

        ValidatableResponse getOrderResponse = orderClient.getOrderDataForUserWithAuth(accessToken);
        int statusCode = getOrderResponse.extract().statusCode();
        boolean isOrderGot = getOrderResponse.extract().path("success");
        OrderData orderData = orderClient.orderData(accessToken);

        assertEquals("Статус код не совпадает", 200, statusCode);
        assertTrue("Заказы не получены", isOrderGot);
        assertThat("Нет заказов", orderData.getOrders(), is(not(empty())));
        assertThat("Хэш не совпадает", orderData.getOrders().get(0).getIngredients(), equalTo(expected));
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Неавторизованный пользователь")
    public void getOrderForUserWithoutAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));
        orderClient.createOrderWithAuth(accessToken, order);

        ValidatableResponse getOrderResponse = orderClient.getOrderDataWithoutAuth();
        int statusCode = getOrderResponse.extract().statusCode();
        boolean isOrderGot = getOrderResponse.extract().path("success");
        String message = getOrderResponse.extract().path("message");

        assertEquals("Статус код не сопадает", 401, statusCode);
        assertFalse("Заказы получены", isOrderGot);
        assertEquals("Сообщение об ошибке не совпадает", "You should be authorised", message);
    }
}
