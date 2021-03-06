package server;

import database.DatabaseConnection;

public class DbAuthService implements AuthService{
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return DatabaseConnection.getNickname(login);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return DatabaseConnection.createNewUser(login, password, nickname);
    }

    public boolean checkAuthorization(String login, String password) {
        return DatabaseConnection.checkAuthorization(login, password);
    }

    public void changeNick(String login, String newNickname) {
        DatabaseConnection.changeNick(login, newNickname);
    }

}
