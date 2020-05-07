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
     * @param username username dell'utente loggato
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
        try {
            this.input.close();
            this.output.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                    if(comando.equals("invia_messaggio")) {
                        if(req.getString("type").equals("text")) {
                            JSONObject finalReq = req;
                            Platform.runLater(() -> this.client_controller.addMessaggio(new Messaggio(finalReq.getInt("id"), finalReq.getString("data"), finalReq.getString("sorgente"), finalReq.getString("destinatario"), finalReq.getString("type"))));
                        }
                    } else if (comando.equals("elimina_messaggio")) {
                        JSONObject finalReq = req;
                        Platform.runLater(() -> this.client_controller.eliminaMessaggio(finalReq.getInt("id_messaggio"), finalReq.getString("sorgente")));
                    }
                }
            } catch (IOException e) {
            }
        }
    }

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

            if(resp.getString("risultato").equals("true")) {
                id = resp.getInt("inserted_id");
            }

            disconnect(out);
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return id;
    }

    private void setUsernameServerSocket() {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "serversocket");
        json_r.put("comando", "set_username");
        json_r.put("username", this.username);

        this.output.println(json_r);
    }

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
