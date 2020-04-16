package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

// riceve i messaggi da un client e li inoltra al destinatario
public class GestoreClient implements Runnable {
    private Server server;
    private Socket client;
    private PrintWriter output;
    private BufferedReader input;

    public GestoreClient(Server server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            init();
            // ascolta l'arrivo di messaggi finchè il client è connesso
            while (client.isConnected()) {
                // riceve il messaggio
                String messaggio = input.readLine();
// TODO: invia messaggio al destinatario
                // prende i client connessi
                ArrayList<GestoreClient> clients = server.getClients();
                // ricerca del destinatario tra i client
                for (GestoreClient c : clients) {

                }
            }
            destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // inizializza gli stream
    private void init() throws IOException {
        output = new PrintWriter(client.getOutputStream());
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    // chiude gli stream ed i socket
    private void destroy() throws IOException {
        output.close();
        input.close();
        client.close();
    }

    // invia un messaggio al client connesso
    public void inviaMessaggio(String messaggio) {
        output.println(messaggio);
        output.flush();
    }
}
