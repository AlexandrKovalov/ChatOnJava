package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;


    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        new Thread(() -> {
            work();
        }).start();
    }

    private void work () {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            //Цикл работы
            while (true) {
                String message = in.readUTF();


                if ("/exit".equals(message)) {
                    System.out.println("Клиент отключился");
                    break;
                }
                server.broadcastMessage(message);
                System.out.println("Client: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
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
}
