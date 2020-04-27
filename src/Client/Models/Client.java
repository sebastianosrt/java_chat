package Client.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import Client.Controllers.ClientController;
import org.json.JSONArray;
import org.json.JSONObject;

public class Client implements Runnable {

    private ClientController client_controller;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private String username;
    private ArrayList<String> contatti;
    private String contatto_selezionato;

    /**
     *
     * @param username username dell'utente loggato
     */
    public Client(String username, ClientController client_controller) {
        this.client_controller = client_controller;
        this.username = username;
        this.contatti = new ArrayList<String>();

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

        this.setContattiByDataBase();
        this.client_controller.caricaContatti(this.contatti);
    }

    @Override
    public void run() {

    }

    public void inviaMessaggio(String messaggio) {

    }

    private void setUsernameServerSocket() {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "serversocket");
        json_r.put("comando", "set_username");
        json_r.put("username", this.username);

        this.output.println(json_r);
        this.output.flush();
    }

    private void setContattiByDataBase() {

        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", this.username);
        json_r.put("destinatario", "database");
        json_r.put("comando", "get_contatti");

        this.output.println(json_r);
        this.output.flush();
        try {
            JSONObject resp = new JSONObject(this.input.readLine());

            if(resp.getString("risultato").equals("true")) {
                JSONArray contatti_array = resp.getJSONArray("lista_contatti");
                int contatti_length = contatti_array.length();
                for(int i = 0; i < contatti_length; i++) {
                    this.contatti.add(contatti_array.getString(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
