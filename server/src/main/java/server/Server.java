package server;

import commands.Commands;
import server.authentication.AuthService;
import server.authentication.SimpleAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private ServerSocket server;
    private Socket socket;
    private final int PORT = 7777;

    private List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private AuthService authService;

    public Server() {
        authService = new SimpleAuthService();
        try {
            server = new ServerSocket(Commands.PORT);
            System.out.println("Сервер успешно запущен");


            while (true) {
                socket = server.accept();
                System.out.println("Клиент успешно соединился по порту " + socket.getPort());
                new ClientHandler(socket, this, authService );

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           try {
               socket.close();
               server.close();
           } catch (IOException e) {
               //игнорируем ошибку, потому что если сокет не удалось закрыть значит он уже закрыт
           }
        }

    }

    public void broadcastMessage(String message) {
        for (ClientHandler client: clients) {
            client.sendMessage(message);
        }
    }

    public void subscribe (ClientHandler handler) {
        clients.add(handler);
    }
    public void unsubscribe(ClientHandler handler) {
        clients.remove(handler);
    }

}
