package User;

public class UserLogin {

    public final String email;
    public final String password;

    public UserLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserLogin from (User user){

        return new UserLogin(user.email, user.password);
    }
}
