package server;

import database.DatabaseConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    private ExecutorService executorService;

    public DbAuthService getDbAuthService() {
        return dbAuthService;
    }

    private DbAuthService dbAuthService;

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();
        dbAuthService = new DbAuthService();
        executorService = Executors.newCachedThreadPool();
        ServerSocket server = null;
        Socket socket;

        final int PORT = 8189;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");
            try {
                DatabaseConnection.connect();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился ");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            DatabaseConnection.disconnect();
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String nick, String msg) {
        for (ClientHandler c : clients) {
            c.sendMsg(nick + ": " + msg);
        }
        Date date = new Date();
        SimpleDateFormat pattern = new SimpleDateFormat("dd.MM 'at' HH:mm");
        String stringDate = pattern.format(date);
        DatabaseConnection.addBroadcastMessage(nick, msg, stringDate);
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s",
                sender.getNick(), receiver, msg);

        for (ClientHandler c : clients) {
            if (c.getNick().equals(receiver)) {
                c.sendMsg(message);
                if (!sender.getNick().equals(receiver)) {
                    sender.sendMsg(message);

                    Date date = new Date();
                    SimpleDateFormat pattern = new SimpleDateFormat("dd.MM 'at' HH:mm");
                    String stringDate = pattern.format(date);
                    DatabaseConnection.privateMessage(sender.getNick(), receiver, msg, stringDate);

                }
                return;
            }
        }

        sender.sendMsg("not found user: " + receiver);
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoginAuthorized(String login) {
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist ");

        for (ClientHandler c : clients) {
            sb.append(c.getNick()).append(" ");
        }
        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }
}
