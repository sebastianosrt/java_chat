package Client.Models;

import Client.Controllers.ClientController;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private ClientController client_controller;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private String username;
    private String contatto_selezionato;

    /**
     *
     * @param username username dell'utente loggato
     */
    public Client(String username, ClientController client_controller) {
        this.client_controller = client_controller;
        this.username = username;

        try {
            this.socket = new Socket("localhost", 666);
            this.output = new PrintWriter(this.socket.getOutputStream(), false);
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }

        this.init();
    }

    private void init() {
        this.setUsernameServerSocket();
        Platform.runLater(() -> this.client_controller.caricaContatti(this.getContattiFromDataBase()));
    }

    @Override
    public void run() {

    }

    public ArrayList<String> searchUsers(String utente) {
        ArrayList<String> lista_utenti = new ArrayList<String>();
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "cerca_utenti");
        json_r.put("nome_utente", utente);

        this.output.println(json_r.toString());
        this.output.flush();

        try {
            JSONObject resp = new JSONObject(this.input.readLine());

            if(resp.getString("risultato").equals("true")) {
                JSONArray utenti_array = resp.getJSONArray("username_trovati");
                int utenti_length = utenti_array.length();
                for(int i = 0; i < utenti_length; i++) {
                    lista_utenti.add(utenti_array.getString(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista_utenti;
    }

    public void inviaMessaggio(String messaggio) {

    }

    private void setUsernameServerSocket() {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "serversocket");
        json_r.put("comando", "set_username");
        json_r.put("username", this.username);

        this.output.println(json_r.toString());
        this.output.flush();
    }

    public ArrayList<String> getContattiFromDataBase() {
        ArrayList<String> contatti = new ArrayList();
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "get_contatti");

        this.output.println(json_r.toString());
        this.output.flush();
        try {
            JSONObject resp = new JSONObject(this.input.readLine());

            if(resp.getString("risultato").equals("true")) {
                JSONArray contatti_array = resp.getJSONArray("lista_contatti");
                int contatti_length = contatti_array.length();
                for(int i = 0; i < contatti_length; i++) {
                    contatti.add(contatti_array.getString(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contatti;
    }
}
