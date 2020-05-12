package Server;

import Server.MySQL.MySQL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Apre il server, si connette al database ed accetta le connessioni
 * @author Sebastiano Sartor
 */
class Server implements Runnable {
    private ArrayList<GestoreClient> clients = new ArrayList<>();

    @Override
    public void run() {
        try {
            MySQL.openConnection();
            // creo il server socket
            ServerSocket server = new ServerSocket(666);
            // ascolta all'infinito richieste di connessione
            while (!server.isClosed()) {
                // ricevi connessione da un client
                Socket socket = server.accept();
                // quando riceve una connessione aggiunge un nuovo client alla lista e crea un nuovo gestore client
                GestoreClient client = new GestoreClient(this, socket);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.out.println("");
        }
        MySQL.closeConnection();
    }

    /**
     * Ritorna i client connessi
     * @return clients - lista di gestore client
     */
    public ArrayList<GestoreClient> getClients() {
        return clients;
    }

    /**
     * rimuove un gestore client dalla lista
     * @param client - il gestore client da rimuovere
     */
    public void rimuoviClient(GestoreClient client) {
        clients.remove(client);
    }
}
