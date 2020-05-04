package Client.Models;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Autenticazione {
    private static Socket socket;
    private static PrintWriter output;
    private static BufferedReader input;

    /**
     * Questo metodo inizializza il socket ed i flussi
     */
    private static void init() throws IOException {
        try {
            socket = new Socket("localhost", 666);
        } catch (IOException e) {
        }
        output = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
        input = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
    }

    /**
     * Questo metodo chiude i flussi ed il socket
     */
    private static void destroy() {
        try {
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
        }
    }

    /**
     *
     * @param username username da loggare
     * @param password password da loggare
     * @return stringa nulla se è andato tutto a buon fine
     */
    public static String login(String username, String password) {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", "");
        json_r.put("destinatario", "database");
        json_r.put("comando", "login");
        json_r.put("username", username);
        json_r.put("password", password);
        JSONObject resp = null;

        try {
            init();
            output.println(json_r.toString());
            resp = new JSONObject(input.readLine());
        } catch (IOException e) {
            return "errore interno";
        }

        destroy();

        if (resp == null)
            return "errore interno";
        if(resp.getString("risultato").equals("true"))
            return "";
        else
            return resp.getString("errore");
    }

    /**
     *
     * @param username username dell'utente da registrare
     * @param password password dell'utente da registrare
     * @return stringa nulla se è andato tutto a buon fine
     */
    public static String signup(String username, String password) {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", "");
        json_r.put("destinatario", "database");
        json_r.put("comando", "registrazione");
        json_r.put("username", username);
        json_r.put("password", password);

        try {
            init();
            output.println(json_r.toString());
            output.flush();
            JSONObject resp = new JSONObject(input.readLine());

            destroy();

            if(resp.getString("risultato").equals("true")) {
                return "";
            } else {
                return resp.getString("errore");
            }
        } catch(IOException e) {
            return "connessione non riuscita";
        }
    }

}
