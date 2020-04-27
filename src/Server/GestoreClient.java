package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import Server.MySQL.MySQL;
import org.json.*;

/**
 * Questa classe riceve i messaggi da un client ed
 * esegue un comando dato
 * @author Sebastiano Sartor
 * */
class GestoreClient implements Runnable {
    private Server server;
    private Socket client;
    private PrintWriter output;
    private BufferedReader input;
    private String username = null;

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
                // riceve il messaggio che sarà una JSON string
                String messaggio = input.readLine();
                if (messaggio != null) {
                    JSONObject object = new JSONObject(messaggio);
                    String comando = object.getString("comando");

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
                        setUsername(object.getString("username"));
                    } else if(comando.equals("login")) {
                        JSONObject res = MySQL.authentication(object.getString("username"), object.getString("password"));
                        output.println(res.toString());
                    } else if(comando.equals("registrazione")) {
                        JSONObject res = MySQL.addUser(object.getString("username"), object.getString("password"));
                        output.println(res);
                    } else if(comando.equals("get_contatti")) {
                        JSONObject res = MySQL.getListContacts(object.getString("sorgente"));
                        output.println(res);
                    } else if(comando.equals("add_contatto")) {
                        JSONObject res = MySQL.addContact(object.getString("sorgente"), object.getString("contatto"));
                        output.println(res);
                    } else if(comando.equals("cerca_utenti")) {
                        JSONObject res = MySQL.searchUser(object.getString("nome_utente"));
                        output.println(res);
                    } else if(comando.equals("get_messaggi")) {
                    } else if(comando.equals("elimina_messaggio")) {
                    }
                }
            }
            // quando il client si disconnette o avviene una SQLException
            destroy();
        } catch (IOException | SQLException e) {
            // client si disconnette
            try {
                destroy();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * inizializza gli stream
     * @throws IOException
     */
    private void init() throws IOException {
        output = new PrintWriter(client.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    /**
     * chiude gli stream ed i socket e rimuove un gestore client
     * @throws IOException
     */
    private void destroy() throws IOException {
        server.rimuoviClient(this);
        output.close();
        input.close();
        client.close();
    }

    /**
     * invia un messaggio al client connesso
     * @param messaggio
     */
    public void inviaMessaggio(String messaggio) {
        output.println(messaggio);
        output.flush();
    }

    /**
     * setta l'username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * ritorna l'username
     * @return
     */
    public String getUsername() {
        return username;
    }
}
