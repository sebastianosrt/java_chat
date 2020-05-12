package Client.Models;

import Client.Controllers.ClientController;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {
    private ClientController client_controller;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private String username;

    /**
     *
     * @param username - username dell'utente loggato
     */
    public Client(String username, ClientController client_controller) {
        this.client_controller = client_controller;
        this.username = username;

        try {
            this.socket = new Socket("localhost", 666);
            this.output = new PrintWriter(new BufferedOutputStream(this.socket.getOutputStream()), true);
            this.input = new BufferedReader(new InputStreamReader(new BufferedInputStream(this.socket.getInputStream())));
        } catch(IOException e) {
            e.printStackTrace();
        }

        this.init();
    }

    /**
     * Questo metodo inizializza la l'oggetto istanziato
     */
    private void init() {
        this.setUsernameServerSocket();
        Platform.runLater(() -> this.client_controller.caricaContatti(this.getContattiFromDataBase()));
    }

    /**
     * Questo metodo serve per inviare il comando di disconnessione
     * @param p - serve per inviare il comando al server
     */
    private void disconnect(PrintWriter p) {
        JSONObject json_r = new JSONObject();
        json_r.put("comando", "disconnect");
        p.println(json_r);
    }

    /**
     * Questo metodo chiude i flussi ed il socket
     */
    public void destroy() {
        this.disconnect(this.output);
        this.exit();
    }

    /**
     * Questo metodo è richiamato dal thread dell'oggetto per ricevere comandi dal ServerSocket
     */
    @Override
    public void run() {
        JSONObject req;
        String comando;
        while(!this.socket.isClosed()) {
            try {
                String res = this.input.readLine();
                if (res != null) {
                    req = new JSONObject(res);
                    comando = req.getString("comando");
                    switch (comando) {
                        case "invia_messaggio":
                            if (req.getString("type").equals("text")) {
                                JSONObject finalReq = req;
                                Platform.runLater(() -> this.client_controller.addMessaggio(new Messaggio(finalReq.getInt("id"), finalReq.getString("data"), finalReq.getString("sorgente"), finalReq.getString("destinatario"), finalReq.getString("type"))));
                            }
                            break;
                        case "elimina_messaggio": {
                            JSONObject finalReq = req;
                            Platform.runLater(() -> this.client_controller.eliminaMessaggio(finalReq.getInt("id_messaggio"), finalReq.getString("sorgente")));
                            break;
                        }
                        case "invia_file": {
                            JSONObject finalReq = req;
                            Platform.runLater(() -> this.client_controller.addFile(finalReq.getString("sorgente"), finalReq.getString("file_name"), finalReq.getInt("id")));
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("");
            }
        }
    }

    /**
     * Questo metodo serve per effettuare la ricerca di un utente
     * @param utente - username dell'utente da cercare
     * @return ritorna una lista di username (utenti)
     */
    public ArrayList<String> searchUsers(String utente) {
        ArrayList<String> lista_utenti = new ArrayList<>();
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "cerca_utenti");
        json_r.put("nome_utente", utente);

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(json_r.toString());
            JSONObject resp = new JSONObject(in.readLine());

            if(resp.getString("risultato").equals("true")) {
                JSONArray utenti_array = resp.getJSONArray("username_trovati");
                int utenti_length = utenti_array.length();
                for(int i = 0; i < utenti_length; i++) {
                    lista_utenti.add(utenti_array.getString(i));
                }
            }

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista_utenti;
    }

    /**
     * Questo metodo serve per aggiungere un utente ai propri contatti
     * @param username_contatto - username dell'utente da aggiungere
     */
    public void addContactToDataBase(String username_contatto) {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "serversocket");
        json_r.put("comando", "add_contatto");
        json_r.put("contatto", username_contatto);

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(json_r.toString());
            JSONObject resp = new JSONObject(in.readLine());

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo invia un messaggio testuale
     * @param contatto - username del destinatario
     * @param messaggio - testo del messaggio
     * @return ritorna l'id del messaggio da aggiungere alla view
     */
    public int inviaMessaggio(String contatto, String messaggio) {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", contatto);
        json_r.put("comando", "invia_messaggio");
        json_r.put("data", messaggio);
        json_r.put("type", "text");

        int id = -1;
        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(json_r.toString());
            JSONObject resp = new JSONObject(in.readLine());

            if(resp.getString("risultato").equals("true"))
                id = resp.getInt("inserted_id");

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return id;
    }

    /**
     * Invia un file
     * @author Sebastiano Sartor
     * @param contatto - il nome del contatto a cui inviare il file
     * @param path - il percorso del file
     * @param fileName -  il nome del file
     * @return ritorna l'id del messaggio da aggiungere alla view
     */
    public int inviaFile(String contatto, String path, String fileName) {
        int id = -1;
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", contatto);
        json_r.put("comando", "invia_file");

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            // invia una prima richiesta avvisando il server che starà per ricevere un file
            out.println(json_r);

            // leggo file e prendo la dimensione
            FileInputStream fs = new FileInputStream(path);
            int size = (int) fs.getChannel().size();
            // invio dimensione file e nome file
            json_r = new JSONObject();
            json_r.put("size", size);
            json_r.put("file_name", fileName);
            out.println(json_r);
            // invio i bytes
            byte[] b = new byte[size];

            //thread che legge il file e lo invia
            new Thread(() -> {
                try {
                    fs.read(b, 0, b.length);
                    OutputStream os = s.getOutputStream();
                    os.write(b, 0, b.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            // prendo l'id del messaggio dalla risposta
            id = new JSONObject(in.readLine()).getInt("inserted_id");
            // disconnessione
            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Scarica un file dal server
     * @author Sebastiano Sartor
     * @param fileName - nome del file da scaricare
     * @param filepath - percorso dove salvare il file
     */
    public void getFile(String fileName, String filepath) {
        JSONObject json_r = new JSONObject();
        json_r.put("comando", "get_file");
        json_r.put("file_name", fileName);

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            // richiesta al server
            out.println(json_r);

            // ricevo la grandezza del file che sto per ricevere
            JSONObject res = new JSONObject(in.readLine());
            int size = res.getInt("size");

            // legge dallo stream finchè non ha letto tutta la grandezza del file, e lo scrive nel percorso specificato
            InputStream is = s.getInputStream();
            try {
                FileOutputStream fo = new FileOutputStream(filepath + "\\" + fileName);
                int count;
                byte[] buffer = new byte[size]; // or 4096, or more
                while (fo.getChannel().size() < size-1 && (count = is.read(buffer)) > 0)
                    fo.write(buffer, 0, count);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // disconnessione
            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo invia e setta lo username dell'utente loggato al ServerSocket
     * per riconoscerlo quando invia o riceve un messaggio
     */
    private void setUsernameServerSocket() {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "serversocket");
        json_r.put("comando", "set_username");
        json_r.put("username", this.username);

        this.output.println(json_r);
    }

    /**
     * Questo metodo recupera i contatti dell'utente appena loggato
     * @return ritorna una lista di username (contatti)
     */
    public ArrayList<String> getContattiFromDataBase() {
        ArrayList<String> contatti = new ArrayList<>();
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "get_contatti");

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(json_r.toString());
            JSONObject resp = new JSONObject(in.readLine());

            if(resp.getString("risultato").equals("true")) {
                JSONArray contatti_array = resp.getJSONArray("lista_contatti");
                int contatti_length = contatti_array.length();
                for(int i = 0; i < contatti_length; i++) {
                    contatti.add(contatti_array.getString(i));
                }
            }

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contatti;
    }

    /**
     * Questo motodo recupera i messaggi inviati o ricevuti con un determinato contatto
     * @param contatto - username del contatto
     * @return ritorna una lista di messaggi
     */
    public ArrayList<Messaggio> getMessaggiFromDataBase(String contatto) {
        ArrayList<Messaggio> messaggi = new ArrayList<>();
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "get_messaggi");
        json_r.put("contatto", contatto);

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(json_r.toString());
            JSONObject resp = new JSONObject(in.readLine());
            if(resp.getString("risultato").equals("true")) {
                JSONArray messaggi_array = resp.getJSONArray("lista_messaggi");
                int messaggi_length = messaggi_array.length();
                for(int i = 0; i < messaggi_length; i++) {
                    JSONObject messaggio = messaggi_array.getJSONObject(i);
                    messaggi.add(new Messaggio(messaggio.getInt("id"), messaggio.getString("data"), messaggio.getString("mittente"), messaggio.getString("destinatario"), messaggio.getString("type")));
                }
            }

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return messaggi;
    }

    /**
     * Questo metodo elimina un determinato messaggio già inviato o ricevuto
     * @param id_messaggio - ID del messaggio da eliminare
     * @param contatto - username del contatto che ha ricevuto o inviato il messaggio da eliminare
     */
    public void eliminaMessaggio(int id_messaggio, String contatto) {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "elimina_messaggio");
        json_r.put("id_messaggio", id_messaggio);
        json_r.put("contatto", contatto);

        try {
            Socket s = new Socket("localhost", 666);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(json_r.toString());
            JSONObject resp = new JSONObject(in.readLine());

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo termiana la connessione con il ServerSocket
     */
    public void exit() {
        try {
            this.socket.close();
            this.input.close();
            this.output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
