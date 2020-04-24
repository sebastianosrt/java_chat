package Server;

import Server.MySQL.MySQL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Sebastiano Sartor
 */
class Server implements Runnable {
    private ArrayList<GestoreClient> clients = new ArrayList<>();

    @Override
    public void run() {
        try {
            MySQL.openConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
        try {
            MySQL.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * ritorna i client connessi
     * @return clients
     */
    public ArrayList<GestoreClient> getClients() {
        return clients;
    }

    /**
     * rimuove un client dalla lista
     * @param client
     */
    public void rimuoviClient(GestoreClient client) {
        clients.remove(client);
    }
}
