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
    private String username;

    public GestoreClient(Server server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            init();
            // ricevo l'username dal client gestito
            username = input.readLine();
            // ascolta l'arrivo di messaggi finchè il client è connesso
            while (client.isConnected()) {
                // riceve il messaggio che sarà composto così: "username-destinatario messaggio"
                String[] messaggio = input.readLine().split(" ", 2);
                String destinatario = messaggio[0];
                String testo = messaggio[1];
                // prende i client connessi
                ArrayList<GestoreClient> clients = server.getClients();
                // ricerca del destinatario tra i client ed invia il messaggio
                for (GestoreClient c : clients)
                    if (c.getUsername().equals(destinatario))
                        c.inviaMessaggio(testo);
                // TODO: salvataggio nel database
            }
            // quando il client si disconnette
            destroy();
        } catch (IOException e) {
            // client si disconnette
            try {
                destroy();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // inizializza gli stream
    private void init() throws IOException {
        output = new PrintWriter(client.getOutputStream());
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    // chiude gli stream ed i socket e rimuove un gestore client
    private void destroy() throws IOException {
        server.rimuoviClient(username);
        output.close();
        input.close();
        client.close();
    }

    // invia un messaggio al client connesso
    public void inviaMessaggio(String messaggio) {
        output.println(messaggio);
        output.flush();
    }

    // ritorna l'username
    public String getUsername() {
        return username;
    }
}
