package Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            while (client.isConnected()) {
                // riceve il messaggio che sarà una JSON string
                String messaggio = input.readLine();
                if (messaggio != null) {
                    JSONObject req = new JSONObject(messaggio);
                    JSONObject res = null;
                    String comando = req.getString("comando");

                    if (comando.equals("invia_messaggio")) {
                        res = MySQL.addMessage(req.getString("sorgente"), req.getString("destinatario"), req.getString("type"), req.getString("data"));
                        if (res.getString("risultato").equals("true")) {
                            output.println(res);
                            req.put("id", res.getInt("inserted_id"));
                            // converto la stringa in oggetto e prendo il valore del campo destinatario
                            String destinatario = req.getString("destinatario");
                            // prende i client connessi
                            ArrayList<GestoreClient> clients = server.getClients();
                            // ricerca del destinatario tra i client ed invia il messaggio
                            boolean sent = false;
                            for (GestoreClient c : clients) {
                                if (c.getUsername() != null && c.getUsername().equals(destinatario)) {
                                    c.inviaMessaggio(req.toString());
                                    sent = true;
                                }
                            }
                            if (!sent)
                                MySQL.addContact(req.getString("destinatario"), req.getString("sorgente"));
                        }
                    }
                    else if (comando.equals("invia_file")) {
                        String mittente = req.getString("sorgente");
                        String destinatario = req.getString("destinatario");

                        req = new JSONObject(input.readLine());
                        int size = req.getInt("size");
                        String filename = req.getString("file_name");

                        String path = new File(".").getCanonicalPath();
                        path += "\\Files\\";

//                        while (!new File(path + filename).exists())
//                            filename = "a" + filename;
                        InputStream is = client.getInputStream();
                        FileOutputStream fo = new FileOutputStream(path + filename);
                        int count;
                        byte[] buffer = new byte[size]; // or 4096, or more
                        while (fo.getChannel().size() < size-1 && (count = is.read(buffer)) > 0)
                            fo.write(buffer, 0, count);

                        int id = 0;
//                        id = MySQL.addFile(mittente, destinatario, filename)

                        boolean sent = false;
                        ArrayList<GestoreClient> clients = server.getClients();
                        req = new JSONObject();
                        req.put("sorgente", mittente);
                        req.put("destinatario", destinatario);
                        req.put("comando", "invia_file");
                        req.put("file_name", filename);
                        req.put("id", id);
                        for (GestoreClient c : clients) {
                            if (c.getUsername() != null && c.getUsername().equals(destinatario)) {
                                c.inviaMessaggio(req.toString());
                                sent = true;
                            }
                        }
                        if (!sent)
                            MySQL.addContact(destinatario, mittente);
                    }
                    else if(comando.equals("set_username"))
                        setUsername(req.getString("username"));
                    else if(comando.equals("login"))
                        res = MySQL.authentication(req.getString("username"), req.getString("password"));
                    else if(comando.equals("registrazione"))
                        res = MySQL.addUser(req.getString("username"), req.getString("password"));
                    else if(comando.equals("get_contatti"))
                        res = MySQL.getListContacts(req.getString("sorgente"));
                    else if(comando.equals("add_contatto"))
                        res = MySQL.addContact(req.getString("sorgente"), req.getString("contatto"));
                    else if(comando.equals("cerca_utenti"))
                        res = MySQL.searchUser(req.getString("nome_utente"));
                    else if(comando.equals("get_messaggi"))
                        res = MySQL.getMessaggi(req.getString("sorgente"), req.getString("contatto"));
                    else if(comando.equals("elimina_messaggio")) {
                        res = MySQL.deleteMessage(req.getInt("id_messaggio"));
                        if(res.getString("risultato").equals("true")) {
                            String destinatario = req.getString("contatto");
                            ArrayList<GestoreClient> clients = server.getClients();
                            for (GestoreClient c : clients)
                                if (c.getUsername() != null && c.getUsername().equals(destinatario))
                                    c.inviaMessaggio(req.toString());
                        }
                    }
                    else if(comando.equals("disconnect"))
                        this.destroy();
                    if (res != null)
                        output.println(res);
                }
            }
            this.destroy();
        } catch (IOException | SQLException e) {
            // client si disconnette
            try {
                this.destroy();
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
