package User;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class ChangeParamsUserTest {
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
    @Description("Изменение данных пользователя")
    @DisplayName("С авторизацией")
    @Step("Готовим тестовые данные")
    public void userEmailCanBeChangedWithAuth() {
        User user = User.getRandom();
        userApi.createUser(user);
        ValidatableResponse loginResponse = userApi.loginUser(UserLogin.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        User userWithChangedEmail = new User(user.email + "1", user.password, user.name);

        ValidatableResponse patchUserInfoResponse = userApi.patchUserInfo(accessToken, userWithChangedEmail);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        LinkedHashMap userNewEmail = patchUserInfoResponse.extract().path("user");

        assertThat("Информация о пользователе не изменена", statusCode, equalTo(SC_OK));
        assertTrue("Информация о пользователе не изменена", isUserInfoChanged);
        assertTrue(userNewEmail.containsValue((user.email + "1").toLowerCase()));
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("С авторизацией")
    @Step("Готовим тестовые данные")
    public void userPasswordCanBeChangedWithAuth() {
        User user = User.getRandom();
        userApi.createUser(user);
        ValidatableResponse loginResponse = userApi.loginUser(UserLogin.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        User userWithChangedPassword = new User(user.email, user.password + "1", user.name);

        ValidatableResponse patchUserInfoResponse = userApi.patchUserInfo(accessToken, userWithChangedPassword);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        LinkedHashMap userNewPassword = patchUserInfoResponse.extract().path("user");

        assertThat("Информация о пользователе не изменена", statusCode, equalTo(SC_OK));
        assertTrue("Информация о пользователе не изменена", isUserInfoChanged);
        assertTrue(userNewPassword.containsValue(user.name));
        assertTrue(userNewPassword.containsValue((user.email).toLowerCase()));
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("С авторизацией")
    @Step("Готовим тестовые данные")
    public void userNameCanBeChangedWithAuth() {
        User user = User.getRandom();
        userApi.createUser(user);
        ValidatableResponse loginResponse = userApi.loginUser(UserLogin.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        User userWithChangedName = new User(user.email, user.password, user.name + "1");

        // Act
        ValidatableResponse patchUserInfoResponse = userApi.patchUserInfo(accessToken, userWithChangedName);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        LinkedHashMap userNewName = patchUserInfoResponse.extract().path("user");

        // Assert
        assertThat("Информация о пользователе не изменена", statusCode, equalTo(SC_OK));
        assertTrue("Информация о пользователе не изменена", isUserInfoChanged);
        assertTrue(userNewName.containsValue(user.name + "1"));
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("Без авторизации")
    @Step("Готовим тестовые данные")
    public void userEmailCannotBeChangedWithoutAuth() {
        User user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithChangedEmail = new User(user.email + "1", user.password, user.name);

        ValidatableResponse patchUserInfoResponse = userApi.patchUserInfo("", userWithChangedEmail);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        String message = patchUserInfoResponse.extract().path("message");

        // Assert
        assertThat("Информация о пользователе изменена", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("Информация о пользователе изменена", isUserInfoChanged);
        assertEquals("Сообщение об ошибке не совпадает", "You should be authorised", message);
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("Без авторизации")
    @Step("Готовим тестовые данные")
    public void userPasswordCannotBeChangedWithoutAuth() {
        User user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithChangedPassword = new User(user.email, user.password + "1", user.name);

        ValidatableResponse patchUserInfoResponse = userApi.patchUserInfo("", userWithChangedPassword);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        String message = patchUserInfoResponse.extract().path("message");

        assertThat("Информация о пользователе изменена", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("Информация о пользователе изменена", isUserInfoChanged);
        assertEquals("Сообщение об ошибке не совпадает", "You should be authorised", message);
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("Без авторизации")
    @Step("Готовим тестовые данные")
    public void userNameCannotBeChangedWithoutAuth() {
        User user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithChangedName = new User(user.email, user.password, user.name + "1");

        ValidatableResponse patchUserInfoResponse = userApi.patchUserInfo("", userWithChangedName);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        String message = patchUserInfoResponse.extract().path("message");

        assertThat("Информация о пользователе изменена", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("Информация о пользователе изменена", isUserInfoChanged);
        assertEquals("Сообщение об ошибке нн совпадает", "You should be authorised", message);
    }
}
