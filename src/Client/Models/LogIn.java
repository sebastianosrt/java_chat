package Client.Models;

import org.json.JSONObject;
import java.io.IOException;

public class LogIn {

    public static String login(String username, String password) throws IOException {
        JSONObject json_dati = new JSONObject();
        json_dati.put("comando", "query");
        json_dati.put("metodo", "login");
        json_dati.put("username", username);
        json_dati.put("password", password);

        JSONObject json_main = new JSONObject();
        json_main.put("sorgente", "");
        json_main.put("destinatario", "database");
        json_main.put("dati", json_dati);

        // TODO autenticazione

        if(username.equals("admin") && password.equals("admin")) {
            return username;
        } else {
            return null;
        }
    }

}
