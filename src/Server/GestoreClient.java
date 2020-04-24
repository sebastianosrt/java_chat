package Server;

import jdk.nashorn.internal.parser.JSONParser;
import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.json.*;

// riceve i messaggi da un client e li inoltra al destinatario
class GestoreClient implements Runnable {
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
                // riceve il messaggio che sarà una JSON string
                String messaggio = input.readLine();
                String comando = new JSONObject(messaggio).getString("comando");

                if (comando.equals("invia_messaggio")) {
                    // converto la stringa in oggetto e prendo il valore del campo destinatario
                    String destinatario = new JSONObject(messaggio).getString("destinatario");
                    // prende i client connessi
                    ArrayList<GestoreClient> clients = server.getClients();
                    // ricerca del destinatario tra i client ed invia il messaggio
                    for (GestoreClient c : clients)
                        if (c.getUsername().equals(destinatario))
                            c.inviaMessaggio(messaggio);
                    // TODO: salvataggio nel database
                } else if(comando.equals("set_username")) {
                    setUsername(new JSONObject(messaggio).getString("username"));
                } else if(comando.equals("elimina_messaggio")) {
                } else if(comando.equals("get_messaggi")) {
                } else if(comando.equals("get_contatti")) {
                }
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
        server.rimuoviClient(this);
        output.close();
        input.close();
        client.close();
    }

    // invia un messaggio al client connesso
    public void inviaMessaggio(String messaggio) {
        output.println(messaggio);
        output.flush();
    }

    // setta l'username
    public void setUsername(String username) {
        this.username = username;
    }

    // ritorna l'username
    public String getUsername() {
        return username;
    }
}
