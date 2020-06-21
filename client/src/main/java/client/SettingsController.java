package client;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class SettingsController {
    Controller controller;
    public TextField nickField;

    public void changeNick(ActionEvent actionEvent) {
        controller.changeNickname(nickField.getText());
        nickField.clear();
    }
}
