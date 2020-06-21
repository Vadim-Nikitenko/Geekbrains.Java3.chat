package database;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAuthorization(String login, String password) {
        try {
            connect();
            rs = stmt.executeQuery("SELECT * FROM users WHERE login = '" + login + "' AND password = '" + password + "'");
            return rs.getString("login").equals(login) && rs.getString("password").equals(password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return false;
    }

    public static String getNickname(String login) {
        String nick = null;
        try {
            connect();
            rs = stmt.executeQuery("SELECT * FROM users WHERE login = '" + login + "'");
            nick = rs.getString("nickname");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return nick;
    }


    public static boolean createNewUser(String s, String s1, String s2) {
        try {
            connect();
            stmt.executeUpdate("INSERT INTO users (login, password, nickname) VALUES ('" + s + "', '" + s1 + "', '" + s2 + "')");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
        return true;
    }

    public static void changeNick(String login, String newNickname) {
        try {
            connect();
            stmt.executeUpdate("UPDATE users SET nickname = '" + newNickname + "' WHERE login = '" + login + "';");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void addBroadcastMessage(String nick, String msg, String date) {
        try {
            connect();
            connection.setAutoCommit(false);
            rs = stmt.executeQuery("SELECT id FROM users WHERE nickname = '" + nick + "'");
            int nickId = rs.getInt("id");
            stmt.executeUpdate("INSERT INTO messages (sender, receiver, message, msg_time) VALUES ('" + nickId + "', '" + null + "', '" + msg + "', '" + date + "')");
            connection.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void privateMessage(String sender, String receiver, String msg, String date) {
        try {
            connect();
            connection.setAutoCommit(false);
            rs = stmt.executeQuery("SELECT id FROM users WHERE nickname = '" + sender + "'");
            int senderId = rs.getInt("id");
            rs = stmt.executeQuery("SELECT id FROM users WHERE nickname = '" + receiver + "'");
            int receiverId = rs.getInt("id");
            stmt.executeUpdate("INSERT INTO messages (sender, receiver, message, msg_time) VALUES ('" + senderId + "', '" + receiverId + "', '" + msg + "', '" + date + "')");
            connection.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

}
