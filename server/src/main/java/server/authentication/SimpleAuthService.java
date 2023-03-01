package server.authentication;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{


    private ArrayList<UserData> users = new ArrayList<>();
    public SimpleAuthService() {
        users.add(new UserData("qwe", "qwe", "alex"));
        users.add(new UserData("qwer", "qwer", "alexq"));
        users.add(new UserData("qwert", "qwert", "alexw"));

    }
    @Override
    public String getNickName(String login, String password) {
        for (UserData user : users) {
            if (login.equals(user.login) && password.equals(user.password)) {
                return user.nickName;
            }
        }
        return null;
    }

    private class UserData {


        private String login;
        private String password;
        private String nickName;

        public UserData(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }
    }
}
