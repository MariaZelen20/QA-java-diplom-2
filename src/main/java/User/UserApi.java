package User;

import Config.Config;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserApi extends Config {

    private static final String AUTH_PATH = "api/auth/";

    @Step("Создаем пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(user)
                .when()
                .post(AUTH_PATH+"register")
                .then();
    }

    @Step("Удаляем пользователя с токеном")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(AUTH_PATH+"user")
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserLogin credentials) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(credentials)
                .when()
                .post(AUTH_PATH+"login")
                .then();
    }

    @Step("Вносим изменения в информацию о пользователе с токеном и мейлом")
    public ValidatableResponse patchUserInfo(String accessToken, User user) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(AUTH_PATH+"user")
                .then();
    }

}
