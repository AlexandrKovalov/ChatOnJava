package server;


import server.authentication.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static commands.Commands.*;

public class ClientHandler {

    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;

    private AuthService authService;
    private String nickname;


    public ClientHandler(Socket socket, Server server, AuthService authService) {
        this.socket = socket;
        this.server = server;
        this.authService = authService;
        new Thread(() -> {
            work();
        }).start();
    }

    private void work () {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            //Цикл авторизации
            while (true) {
                String message = in.readUTF();

                if (message.startsWith(AUTH)) {
                    String[] parts = message.split(" ");
                    String login = parts[1];
                    String password = parts[2];
                    nickname = authService.getNickName(login, password);

                    if (nickname == null) {
                        out.writeUTF(AUTH_DENIED);
                    } else {
                        out.writeUTF(String.format("%s %s", AUTH_OK, nickname));
                        break;
                    }
                }
            }
            server.subscribe(this);
            //Цикл работы
            while (true) {
                String message = in.readUTF();


                if (EXIT.equals(message)) {
                    System.out.println("Клиент отключился");
                    out.writeUTF(EXIT);
                    break;
                }
                server.broadcastMessage(String.format("[%s]: %s", nickname, message));
                System.out.println("Client: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.unsubscribe(this);
                socket.close();
            } catch (IOException e) {
                //Игноируем ошибку
            }
        }
    }
    public void sendMessage (String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
