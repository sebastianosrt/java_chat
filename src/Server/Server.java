package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    private ArrayList<GestoreClient> clients = new ArrayList<>();

    @Override
    public void run() {
        try {
            // creo il server socket
            ServerSocket server = new ServerSocket(666);

            // ascolta all'infinito richieste di connessione
            while (true) {
                Socket socket = server.accept();
                // quando riceve una connessione aggiunge un nuovo client alla lista
                // e crea un nuovo client handler
                GestoreClient client = new GestoreClient(this, socket);
                clients.add(client);
                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<GestoreClient> getClients() {
        return clients;
    }
}
