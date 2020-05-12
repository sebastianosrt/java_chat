package Server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import Server.MySQL.MySQL;
import org.json.*;

/**
 * Questa classe riceve i messaggi da un client ed esegue un comando dato
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

                    switch (comando) {
                        case "invia_messaggio":
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
                            break;
                        case "invia_file": {
                            String mittente = req.getString("sorgente");
                            String destinatario = req.getString("destinatario");

                            req = new JSONObject(input.readLine());
                            int size = req.getInt("size");
                            byte[] buffer = new byte[size]; // or 4096, or more

                            String filename = req.getString("file_name");

                            String path = new File(".").getCanonicalPath();
                            path += "\\Files\\";

                            InputStream is = client.getInputStream();
                            String finalPath = path;
                            // thread che legge dal socket e scrive il file
                            new Thread(() -> {
                                try {
                                    FileOutputStream fo = new FileOutputStream(finalPath + filename);
                                    int count;
                                    while (fo.getChannel().size() < size - 1 && (count = is.read(buffer)) > 0)
                                        fo.write(buffer, 0, count);
                                } catch (IOException e) {
                                    System.out.println("error");
                                }
                            }).start();

                            int id = 0;

                            res = MySQL.addMessage(mittente, destinatario, "file", filename);
                            output.println(res);

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
                            break;
                        }
                        case "get_file": {
                            String fileName = req.getString("file_name");
                            // leggo file e prendo la dimensione
                            FileInputStream fs = new FileInputStream("./Files/" + fileName);
                            int size = (int) fs.getChannel().size();
                            // invio dimensione file e nome file
                            res = new JSONObject();
                            res.put("size", size);
                            output.println(res);
                            // invio i bytes
                            byte[] b = new byte[size];
                            fs.read(b, 0, b.length);
                            OutputStream os = client.getOutputStream();
                            os.write(b, 0, b.length);
                            os.close();
                            break;
                        }
                        case "set_username":
                            setUsername(req.getString("username"));
                            break;
                        case "login":
                            res = MySQL.authentication(req.getString("username"), req.getString("password"));
                            break;
                        case "registrazione":
                            res = MySQL.addUser(req.getString("username"), req.getString("password"));
                            break;
                        case "get_contatti":
                            res = MySQL.getListContacts(req.getString("sorgente"));
                            break;
                        case "add_contatto":
                            res = MySQL.addContact(req.getString("sorgente"), req.getString("contatto"));
                            break;
                        case "cerca_utenti":
                            res = MySQL.searchUser(req.getString("nome_utente"));
                            break;
                        case "get_messaggi":
                            res = MySQL.getMessaggi(req.getString("sorgente"), req.getString("contatto"));
                            break;
                        case "elimina_messaggio":
                            res = MySQL.deleteMessage(req.getInt("id_messaggio"));
                            if (res.getString("risultato").equals("true")) {
                                String destinatario = req.getString("contatto");
                                ArrayList<GestoreClient> clients = server.getClients();
                                for (GestoreClient c : clients)
                                    if (c.getUsername() != null && c.getUsername().equals(destinatario))
                                        c.inviaMessaggio(req.toString());
                            }
                            break;
                        case "disconnect":
                            this.destroy();
                            break;
                    }
                    if (res != null)
                        output.println(res);
                }
            }
            this.destroy();
        } catch (IOException e) {
            // client si disconnette
            try {
                this.destroy();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Inizializza gli stream
     * @throws IOException
     */
    private void init() throws IOException {
        output = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
        input = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
    }

    /**
     * Chiude gli stream ed i socket e rimuove "this" dalla lista dei gestore client del server
     * @throws IOException
     */
    private void destroy() throws IOException {
        server.rimuoviClient(this);
        output.close();
        input.close();
        client.close();
    }

    /**
     * Invia un messaggio al client connesso
     * @param messaggio - il messaggio da inviare al client connesso
     */
    public void inviaMessaggio(String messaggio) {
        output.println(messaggio);
    }

    /**
     * Setta l'username dell'utente gestito
     * @param username - username da settare
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Ritorna l'username dell'utente gestito
     * @return username - username dell'utente gestito
     */
    public String getUsername() {
        return username;
    }
}
