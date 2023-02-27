package com.example.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;

    private DataInputStream in;

    private DataOutputStream out;
    private Socket socket;

    private final String ADDRESS = "localhost";
    private final int PORT = 7777;

    public void sendMessage(ActionEvent actionEvent) {
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

            //Цикл работы
            while (true) {
                String message = in.readUTF();

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
}