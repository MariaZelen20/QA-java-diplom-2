package User;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class CreateUserTest {
    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void tearDown() {
        try{
            userApi.deleteUser(accessToken);
        } catch(java.lang.IllegalArgumentException error) {
        }
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать уникального пользователя")
    @Step("Готовим тестовые данные")
    public void userCanBeCreated() {
        User user = User.getRandom();

        ValidatableResponse createResponse = userApi.createUser(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        accessToken = createResponse.extract().path("accessToken");

        assertThat("Пользователь не создан", statusCode, equalTo(SC_OK));
        assertTrue("Сообщение об ошибке не совпадает", isUserCreated);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    @Step("Готовим тестовые данные")
    public void userThatExistsCannotBeCreated() {
        User user = User.getRandom();
        ValidatableResponse createResponse = userApi.createUser(user);
        accessToken = createResponse.extract().path("accessToken");

        ValidatableResponse secondCreateResponse = userApi.createUser(user);
        int statusCode = secondCreateResponse.extract().statusCode();
        boolean isUserCreated = secondCreateResponse.extract().path("success");
        String message = secondCreateResponse.extract().path("message");

        assertThat("Пользователь создан", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("Пользователь создан", isUserCreated);
        assertEquals("Сообщение об ошибке не совпадает", "User already exists", message);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей.")
    @Step("Готовим тестовые данные")
    public void userCannotBeCreatedWithoutEmail() {
        User user = User.getRandom();
        User userWithoutEmail = new User("", user.password, user.name);

        ValidatableResponse createResponse = userApi.createUser(userWithoutEmail);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Пользователь создан", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("Пользователь не создан", isUserCreated);
        assertEquals("Сообщение об ошибке не совпадает", "Email, password and name are required fields", message);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей.")
    @Step("Готовим тестовые данные")
    public void userCannotBeCreatedWithoutPassword() {
        User user = User.getRandom();
        User userWithoutPassword = new User(user.email, "", user.name);

        ValidatableResponse createResponse = userApi.createUser(userWithoutPassword);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Пользователь создан", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("Пользователь создан", isUserCreated);
        assertEquals("Сообщение об ошибке не совпадает", "Email, password and name are required fields", message);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей.")
    @Step("Готовим тестовые данные")
    public void userCannotBeCreatedWithoutName() {
        User user = User.getRandom();
        User userWithoutName = new User(user.email, user.password, "");

        ValidatableResponse createResponse = userApi.createUser(userWithoutName);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Пользователь создан", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("Пользователь создан", isUserCreated);
        assertEquals("Сообщение об ошибке не совпадает", "Email, password and name are required fields", message);
    }
}
