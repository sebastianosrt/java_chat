package Client.Models;

import com.mysql.jdbc.Buffer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LogIn {

    public static String login(String username, String password) throws IOException {
        JSONObject json_r = new JSONObject();
        json_r.put("sorgente", "");
        json_r.put("destinatario", "database");
        json_r.put("comando", "login");
        json_r.put("username", username);
        json_r.put("password", password);

        Socket socket = new Socket("localhost", 666);
        PrintWriter output = new PrintWriter(socket.getOutputStream(), false);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        output.println(json_r);
        output.flush();
        JSONObject resp = new JSONObject(input.readLine());

        output.close();
        input.close();
        socket.close();

        if(resp.getString("risultato").equals("true")) {
            return "";
        } else {
            return resp.getString("errore");
        }
    }

}
