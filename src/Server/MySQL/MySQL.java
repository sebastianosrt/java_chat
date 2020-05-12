package Server.MySQL;

import Server.MySQL.MySQLException.*;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQL {

    private final static String URL      = "jdbc:mysql://localhost:3306/";
    private final static String DBNANME  = "java_chat";// progettojava è il nome del database
    private final static String USER     = "java_chat"; // l'utente per accedere al database si chiama allo stesso modo
    private final static String PASSWORD = "java_chat";
    private final static String DRIVER   = "com.mysql.jdbc.Driver";
    private static Connection conn;


    /**
     * Apri la connessione con il database
     * @return Connection
     */
    public static Connection openConnection() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL + DBNANME, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connessione non effettuata " + e.toString());
        }
        return conn;
    }


    /**
     * chiudi la connesione con il database
     */
    public static void closeConnection() {
        try{
            conn.close();
        }catch (SQLException e){
            System.out.println("Errore nella chiusura della connesione al database: " + e.toString());
        }
    }


    /**
     * crea le tabelle per il corretto salvataggio dei dati
     */
    public static void createTables() {
        try {
            boolean statusConn = conn.isClosed();

            if(!statusConn){

                ArrayList<String> tables = new ArrayList<>();

                //qua si possono aggiungere tutte le query per ogni tabella
                tables.add("CREATE TABLE IF NOT EXISTS `users` (`id` int(0) NOT NULL AUTO_INCREMENT,`username` varchar(255) NOT NULL,`password` varchar(255) NOT NULL,`statusClient` varchar(255), `checked` varchar(255), PRIMARY KEY (`id`));"); //tabella utenti

                tables.add("CREATE TABLE IF NOT EXISTS `contacts` (`id` int(0) NOT NULL AUTO_INCREMENT,`user` varchar(255) NOT NULL,`contact` varchar(255) NOT NULL,PRIMARY KEY (`id`));"); //tabella contatti

                tables.add("CREATE TABLE IF NOT EXISTS `messages` (`id` int(0) NOT NULL AUTO_INCREMENT,`mittente` varchar(255) NOT NULL,`destinatario` varchar(255) NOT NULL, `type` varchar(48) NOT NULL DEFAULT 'text', `data` text NOT NULL, PRIMARY KEY (`id`));"); //tabella contatti

                //crea le tabelle
                for(String table : tables){
                    PreparedStatement pstmt = conn.prepareStatement(table);
                    pstmt.executeUpdate();
                }

                System.out.println("Tabelle create nel database!");


            }else{
                System.out.println("Connessione chiusa!");
            }

        }catch (SQLException e){
            System.out.println(e.toString());
        }
    }


    public static boolean existUsername(String username) throws SQLException {
        boolean esiste = false;

        boolean statusConn = conn.isClosed();
        if(!statusConn){
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `users` WHERE `username` = ?");
            pstmt.setString(1, username);

            ResultSet  result = pstmt.executeQuery(); //esegue la query

            //se trova la riga con l'username, allroa result.next NON SARA' VUOTO
            if(result.next()){
                esiste = true;
            }

        }else{
            System.out.println("Connessione chiusa!");
        }

        return esiste;
    }


    /**
     *
     * @param username nome dell'utente da aggiungere
     * @param password password dell'utente da aggiungere
     * @return json response of the result
     */
    public static JSONObject addUser(String username, String password) {
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "aggiungi_utente");
        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){
                boolean esiste = MySQL.existUsername(username);
                if(!esiste){
                    PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `users` (`username`, `password`, `statusClient`) VALUES (?,?,?)");
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.setString(3, "");
                    pstmt.executeUpdate(); //esegue la query

                    response.put("risultato", "true");
                }else{
                    response.put("risultato", "false");
                    response.put("errore", "Username già esistente");
                }
            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }


    /**
     *
     * @param username nome dell'utente da aggiungere
     * @param password password dell'utente da aggiungere
     * @return json response of the result
     */
    public static JSONObject authentication(String username, String password) {

        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "autenticazione");
        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `users` WHERE `username` = ?");
                pstmt.setString(1, username);

                ResultSet result_q = pstmt.executeQuery(); //esegue la query
                //se trova la riga con l'username, allroa result.next NON SARA' VUOTO
                if(result_q.next()){
                    HashMap<String, String> u = MySQL.createRowObj(result_q); //specie di array associativo

                    if(u.get("password").equals(password)){
                        response.put("risultato", "true");
                    }else{
                        response.put("risultato", "false");
                        response.put("errore", "Password Scorretta");
                    }
                }else{
                    response.put("risultato", "false");
                    response.put("errore", "Username inesistente!");
                }
            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }


    /**
     *
     * @param rs risultato della query
     * @return mappa di stringhe (simile ad un array associativo [php])
     * @throws SQLException
     */
    private static HashMap<String, String> createRowObj(ResultSet rs) throws SQLException {
        HashMap<String, String> map = new HashMap<>();

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        for (int i = 1; i <= columnsNumber; i++) {
            map.put(rsmd.getColumnName(i), rs.getString(i));
        }

        return map;
    }


    /**
     *
     * @param user username del profilo autenticato
     * @param contact contatto da cercare
     * @return json response of the result
     * @throws SQLException
     */
    public static boolean existContact(String user, String contact) throws SQLException {
        boolean esiste = false;

        boolean statusConn = conn.isClosed();
        if(!statusConn){
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `contacts` WHERE `user` = ? AND `contact` = ?");
            pstmt.setString(1, user);
            pstmt.setString(2, contact);

            ResultSet  result = pstmt.executeQuery(); //esegue la query

            //se trova la riga con l'username, allora result.next NON SARA' VUOTO
            if(result.next()){
                esiste = true;
            }

        }else{
            System.out.println("Connessione chiusa!");
        }

        return esiste;
    }


    /**
     *
     * @param user username del profilo autenticato
     * @param contact contatto da aggiungere
     * @return json response of the result
     */
    public static JSONObject addContact(String user, String contact) {
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "aggiungi_contatto");

        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){
                boolean esiste_username = MySQL.existUsername(user);

                if(esiste_username){
                    boolean esiste_contatto = MySQL.existContact(user, contact);
                    if(!esiste_contatto){
                        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `contacts` (`user`, `contact`) VALUES (?,?)");
                        pstmt.setString(1, user);
                        pstmt.setString(2, contact);
                        pstmt.executeUpdate(); //esegue la query

                        response.put("risultato", "true");
                    }else{
                        response.put("risultato", "false");
                        response.put("errore", "contact_already_exist");
                    }
                }else{
                    response.put("risultato", "false");
                    response.put("errore", "username_inesistente");
                }

            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }

    /**
     *
     * @param user username dell'utente di cui cercare i contatti
     * @return lista di contatti
     */
    public static JSONObject getListContacts(String user) {
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "get_contatti");

        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){
                boolean esiste_username = MySQL.existUsername(user);

                if(esiste_username){

                    PreparedStatement pstmt = conn.prepareStatement("SELECT `contact` FROM `contacts` WHERE `user` = ?");
                    pstmt.setString(1, user);
                    ResultSet  rs = pstmt.executeQuery(); //esegue la query

                    ArrayList<String> contacts = new ArrayList<>();
                    while (rs.next()){
                        contacts.add(rs.getString("contact"));
                    }
                    response.put("risultato","true");
                    response.put("lista_contatti",contacts);

                }else{
                    response.put("risultato", "false");
                    response.put("errore", "username_inesistente");
                }

            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }

    /**
     *
     * @param s stringa con cui deve inziare l'username
     * @return listq di username trovati
     */
    public static JSONObject searchUser(String s) {
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "cerca_utente");

        //SELECT * from users WHERE username LIKE 'd%'
        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){

                PreparedStatement pstmt = conn.prepareStatement("SELECT `username` from `users` WHERE `username` LIKE '"+s+"%'"); //che iniziano con

                ResultSet  rs = pstmt.executeQuery(); //esegue la query

                if(rs.next()){

                    ArrayList<String> username_found = new ArrayList<>();
                    username_found.add(rs.getString("username"));

                    //se ne trova anche altri
                    while (rs.next()){
                        username_found.add(rs.getString("username"));
                    }

                    response.put("risultato","true");
                    response.put("username_trovati",username_found);
                    response.put("risultato","true");
                }else{
                    response.put("errore","nessun username trovato che inzia con: " + s); //non trovato
                    response.put("risultato","false"); //non trovato
                }

            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }

    /**
     *
     * @param mittente intestario del messaggio inviato
     * @param destinatario ricevente del messaggio inviato
     * @param type tipo di messaggio inviato (testo, file)
     * @param data contenuto del messaggio
     * @return json response of the result with the id of inserted row in the DB
     */
    public static JSONObject addMessage(String mittente, String destinatario, String type, String data) {

        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "aggiungi_messaggio");
        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){

                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `messages` (`mittente`, `destinatario`, `type`, `data`) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mittente);
                pstmt.setString(2, destinatario);
                pstmt.setString(3, type); //default text
                pstmt.setString(4, data);
                pstmt.executeUpdate(); //esegue la query

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    response.put("inserted_id", generatedKeys.getLong(1));
                }
                response.put("risultato", "true");

            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }


    /**
     *
     * @param mittente colui che invia il messaggio
     * @param destinatario colui che riceve il messaggio
     * @return json response of the result
     */
    public static JSONObject getMessaggi(String mittente, String destinatario) {
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "get_messaggi");

        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){

                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `messages` WHERE `mittente` IN (?,?) AND `destinatario` IN (?,?)");
                pstmt.setString(1, mittente);
                pstmt.setString(2, destinatario);
                pstmt.setString(3, mittente);
                pstmt.setString(4, destinatario);
                ResultSet  rs = pstmt.executeQuery(); //esegue la query

                ArrayList<JSONObject> listOfMessages = new ArrayList<>();

                while (rs.next()){
                    /*
                        nel caso bisogni modificare manualmente l'id dei messaggi (impostanto l'ordine crescente di arrivo 1,2,3)
                        HashMap<String,String> temp_map = new HashMap<>();
                        temp_map.put("temp_id", rs.getString(0))
                     */

                    JSONObject temp_message = new JSONObject(MySQL.createRowObj(rs)); //viene passata una HashMap
                    listOfMessages.add(temp_message);
                }

                response.put("lista_messaggi",listOfMessages);
                response.put("risultato","true");

            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }

    /**
     *
     * @param id del messaggio del DB
     * @return content of the message
     */
    public static JSONObject getMessage(int id){
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "getMessage");

        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){

                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `messages` WHERE `id` = ?");
                pstmt.setInt(1, id);
                ResultSet  rs = pstmt.executeQuery(); //esegue la query

                if (rs.next()){
                    JSONObject temp_message = new JSONObject(MySQL.createRowObj(rs)); //viene passata una HashMap

                    response.put("message_record", temp_message);
                    response.put("risultato","true");
                }else{
                    response.put("risultato","false");
                    response.put("errore", "messaggio non trovato con questo id: " + id);
                }


            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return response;
    }

    public static JSONObject deleteMessage(int id){
        JSONObject response = new JSONObject();
        response.put("sorgente", "database");
        response.put("metodo", "delete_message");

        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){

                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `messages` WHERE `id` = ?");
                pstmt.setInt(1, id);
                 //esegue la query
                if (pstmt.executeUpdate() == 1)
                    response.put("risultato","true");
                else
                    response.put("risultato", "false");
            }else{
                response.put("risultato", "false");
                response.put("errore", "connection_closed");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }

        return response;
    }
}