package com.example.client;

import commands.Commands;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static commands.Commands.*;

public class MainController implements Initializable {

    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox messageForm;
    @FXML
    public HBox authForm;

    private DataInputStream in;

    private DataOutputStream out;
    private Socket socket;



    private boolean authenticated;
    private String nickName;


    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        messageForm.setVisible(authenticated);
        messageForm.setManaged(authenticated);
        authForm.setVisible(authenticated);
        authForm.setManaged(authenticated);

        if (authenticated) {
            nickName = "";
        }
    }


    public void sendMessage() {
        String message = textField.getText();
        textField.clear();
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textField.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authenticated = false;
       new Thread(() -> {
           work();
       }).start();

        Platform.runLater(() -> {
            textField.requestFocus();
        });
    }
    private void work() {
        try {
            socket = new Socket(ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            //Цикл авторизации
            while (true) {
                String message = in.readUTF();
                if (message.startsWith(AUTH_OK)) {
                    nickName = message.split(" ")[1];
                    setAuthenticated(true);
                    textArea.clear();
                    break;
                }

                if (message.equals(AUTH_DENIED)) {
                    textArea.appendText("Неверный логин или пароль.");
                }
            }

            //Цикл работы
            while (true) {
                String message = in.readUTF();

                if (message.equals(EXIT)) {
                    setAuthenticated(false);
                    break;
                }

                addMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //игнорируем ошибку, потому что если сокет не удалось закрыть значит он уже закрыт
            }
        }
    }
    private void addMessage(String message) {
        textArea.appendText(message + "\n");
    }

    @FXML
    private void tryToAuth (ActionEvent actionEvent) {
            String login = loginField.getText();
            String password = passwordField.getText();
            passwordField.clear();
            loginField.clear();
            // /auth login password
        try {
            out.writeUTF(String.format("%s %s %s", AUTH, login, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}