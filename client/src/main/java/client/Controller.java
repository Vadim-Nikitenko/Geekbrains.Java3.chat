package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox msgPanel;
    @FXML
    public ListView<String> clientList;
    @FXML
    public MenuBar menu;
    @FXML
    public TextField nickField;
    Stage settingsStage;
    Stage regStage;
    String str;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    private boolean authenticated;
    private String nick;
    private String login;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setManaged(authenticated);
        msgPanel.setVisible(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        menu.setVisible(authenticated);
        menu.setManaged(authenticated);
        if (!authenticated) {
            nick = "";
        }
        textArea.clear();
        setTitle(nick);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);

        regStage = createRegWindow();
        settingsStage = createSettingsWindow();

        Platform.runLater(() -> {
            Stage stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("bue");
                    if (socket != null && !socket.isClosed()) {
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (true) {
                        str = in.readUTF();

                        System.out.println(str);

                        if (str.equals("/end")) {
                            throw new RuntimeException();
                        }

                        if (str.startsWith("/authok ")) {
                            nick = str.split(" ")[1];
                            setAuthenticated(true);
                            LocalHistory.createLocalHistory(login);
                            textArea.appendText(LocalHistory.showHistory(login));
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }

                    //цикл работы
                    while (true) {
                        str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                break;
                            }

                            if (str.startsWith("/clientlist ")) {
                                repaintStage(str);
                            }

                        } else {
                            textArea.appendText(str + "\n");
                            LocalHistory.saveHistory(login, str);
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println("re");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("мы отключились");
                    setAuthenticated(false);
                    this.login = null;
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/auth " + loginField.getText().trim() + " " + passwordField.getText().trim());
            passwordField.clear();
            this.login = loginField.getText().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nick) {
        Platform.runLater(() -> {
            ((Stage) textField.getScene().getWindow()).setTitle("Super chat " + nick);
        });
    }

    public void clickClientList(MouseEvent mouseEvent) {
        System.out.println(clientList.getSelectionModel().getSelectedItem());
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText("/w " + receiver + " ");
    }

    private Stage createRegWindow() {
        Stage stage = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();

            stage = new Stage();
            stage.setTitle("Registration ");
            stage.setScene(new Scene(root, 300, 200));
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);

            RegController regController = fxmlLoader.getController();
            regController.controller = this;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stage;
    }

    public void showRegWindow(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryRegistration(String login, String password, String nickname) {
        String msg = String.format("/reg %s %s %s", login, password, nickname);
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Stage createSettingsWindow() {
        Stage stage = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            Parent root = fxmlLoader.load();

            stage = new Stage();
            stage.setTitle("Settings ");
            stage.setScene(new Scene(root, 300, 200));
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);

            SettingsController settingsController = fxmlLoader.getController();
            settingsController.controller = this;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stage;
    }

    public void changeNickname(String nickname) {
        try {
            out.writeUTF("/changeNick " + nickname.trim());
            setTitle(nickname.trim());
            repaintStage(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSettings(ActionEvent actionEvent) {
        settingsStage.show();
    }

    public void repaintStage(String str) {
        String[] token = str.split(" ");
        Platform.runLater(() -> {
            clientList.getItems().clear();
            for (int i = 1; i < token.length; i++) {
                clientList.getItems().add(token[i]);
            }
        });
    }
}
