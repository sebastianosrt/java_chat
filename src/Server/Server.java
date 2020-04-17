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
                // ricevi connessione da un client
                Socket socket = server.accept();
                // quando riceve una connessione aggiunge un nuovo client alla lista e crea un nuovo gestore client
                GestoreClient client = new GestoreClient(this, socket);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ritorna i client connessi
    public ArrayList<GestoreClient> getClients() {
        return clients;
    }

    // rimuove un client dalla lista
    public void rimuoviClient(String username) {
        for (GestoreClient client : clients)
            if (client.getUsername().equals(username)) {
                clients.remove(client);
                return;
            }
    }
}
