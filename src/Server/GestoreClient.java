package Server;

import java.io.*;
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
        System.out.println("thread creato");
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            init();
            // riceve il messaggio che sarà una JSON string
            String messaggio = input.readLine();
            if (messaggio != null) {
                JSONObject object = new JSONObject(messaggio);
                JSONObject res = null;
                String comando = object.getString("comando");

                if (comando.equals("invia_messaggio")) {
                    res = MySQL.addMessage(object.getString("sorgente"), object.getString("destinatario"), object.getString("type"), object.getString("data"));
                    if (res.getString("risultato").equals("true")) {
                        output.println(res);
                        object.put("id", res.getInt("inserted_id"));
                        // converto la stringa in oggetto e prendo il valore del campo destinatario
                        String destinatario = new JSONObject(messaggio).getString("destinatario");
                        // prende i client connessi
                        ArrayList<GestoreClient> clients = server.getClients();
                        // ricerca del destinatario tra i client ed invia il messaggio
                        for (GestoreClient c : clients)
                            if (c.getUsername() != null && c.getUsername().equals(destinatario))
                                c.inviaMessaggio(object.toString());
                    }
                    this.destroy();
                }
                else if(comando.equals("set_username"))
                    setUsername(object.getString("username"));
                else if(comando.equals("login"))
                    res = MySQL.authentication(object.getString("username"), object.getString("password"));
                else if(comando.equals("registrazione"))
                    res = MySQL.addUser(object.getString("username"), object.getString("password"));
                else if(comando.equals("get_contatti"))
                    res = MySQL.getListContacts(object.getString("sorgente"));
                else if(comando.equals("add_contatto"))
                    res = MySQL.addContact(object.getString("sorgente"), object.getString("contatto"));
                else if(comando.equals("cerca_utenti"))
                    res = MySQL.searchUser(object.getString("nome_utente"));
                else if(comando.equals("get_messaggi"))
                    res = MySQL.getMessaggi(object.getString("sorgente"), object.getString("contatto"));
                else if(comando.equals("elimina_messaggio"))
                    res = MySQL.getMessaggi(object.getString("sorgente"), object.getString("contatto"));
                if (res != null)
                    output.println(res);
            }
            this.destroy();
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
        output = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
        input = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
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
