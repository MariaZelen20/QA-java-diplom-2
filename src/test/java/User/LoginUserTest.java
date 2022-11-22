package User;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class LoginUserTest {
    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void tearDown() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @Description("Логин пользователя")
    @DisplayName("Логин под существующим пользователем")
    @Step("Готовим тестовые данные")
    public void userCanBeLoggedIn() {
        User user = User.getRandom();
        userApi.createUser(user);

        ValidatableResponse loginResponse = userApi.loginUser(UserLogin.from(user));
        int statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");

        assertThat("Авторизация провалена", statusCode, equalTo(SC_OK));
        assertTrue("Авторизация провалена", isUserLoggedIn);
    }

    @Test
    @Description("Логин пользователя")
    @DisplayName("Логин с неверным логином и паролем")
    @Step("Готовим тестовые данные")
    public void userCannotBeLoggedInWithFakeData() {
        User user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithFakeLoginAndPassword = new User(user.email+"1", user.password+"1", user.name );

        ValidatableResponse loginResponse = userApi.loginUser(UserLogin.from(userWithFakeLoginAndPassword));
        int statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        assertThat("Статус код не совпадает", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("Пользователь авторизован", isUserLoggedIn);
        assertEquals("Сообщение об ошибке не совпадает", "email or password are incorrect", message);
    }

}
