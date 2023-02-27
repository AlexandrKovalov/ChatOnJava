package server;

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

    public Server() {


        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер успешно запущен");


            while (true) {
                socket = server.accept();
                System.out.println("Клиент успешно соединился по порту " + socket.getPort());
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
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

}
