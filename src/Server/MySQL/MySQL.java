package Server.MySQL;

import Server.MySQL.MySQLException.*;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQL {

    private final static String URL      = "jdbc:mysql://localhost:3306/";
    private final static String DBNANME  = "progettojava";// progettojava è il nome del database
    private final static String USER     = "progettojava"; // l'utente per accedere al database si chiama allo stesso modo
    private final static String PASSWORD = "pswprogetto";
    private final static String DRIVER   = "com.mysql.jdbc.Driver";
    private static Connection conn;


    /**
     * Apri la connessione con il database
     * @return Connection
     * @throws SQLException
     */
    public static Connection openConnection() throws SQLException {

        try {
            Class.forName(DRIVER);
            conn = (Connection)DriverManager.getConnection(URL + DBNANME, USER, PASSWORD);

            System.out.println("Connessione effettuata dai porco dio");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connessione non effettuata " + e.toString());
        }
        return conn;
    }


    /**
     * chiudi la connesione con il database
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        try{
            conn.close();
            System.out.println("Connessione chiusa correttamente!");
        }catch (SQLException e){
            System.out.println("Errore nella chiusura della connesione al database: " + e.toString());
        }
    }


    /**
     * crea le tabelle per il corretto salvataggio dei dati
     * @throws SQLException
     */
    public static void createTables() throws SQLException {
        try {
            boolean statusConn = conn.isClosed();

            if(!statusConn){

                ArrayList<String> tables = new ArrayList<>();

                //qua si possono aggiungere tutte le query per ogni tabella
                tables.add("CREATE TABLE IF NOT EXISTS `users` (`id` int(0) NOT NULL AUTO_INCREMENT,`username` varchar(255) NOT NULL,`password` varchar(255) NOT NULL,`statusClient` varchar(255),PRIMARY KEY (`id`));"); //tabella utenti

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
     * @return aggiunto true or false
     * @throws SQLException
     */
    public static boolean addUser(String username, String password) throws SQLException, UsernameAlreadyExistException {

        boolean aggiunto = false;
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

                    System.out.println("Utente aggiunto al DB!");
                    aggiunto = true;
                }else{
                    throw new UsernameAlreadyExistException("username già esistente");
                }
            }else{
                System.out.println("Connessione chiusa!");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        return aggiunto;
    }


    /**
     *
     * @param username nome dell'utente da aggiungere
     * @param password password dell'utente da aggiungere
     * @return json response of the result
     * @throws SQLException
     */
    public static JSONObject authentication(String username, String password) throws SQLException, UsernameAlreadyExistException, WrongPasswordException{

        JSONObject r = new JSONObject();
        try {
            boolean statusConn = conn.isClosed();
            if(!statusConn){
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM `users` WHERE `username` = ?");
                pstmt.setString(1, username);

                ResultSet  result = pstmt.executeQuery(); //esegue la query
                //se trova la riga con l'username, allroa result.next NON SARA' VUOTO
                if(result.next()){
                    HashMap<String, String> u = MySQL.createRowObj(result); //specie di array associativo

                    if(u.get("password").equals(password)){
                        System.out.println("password corretta");
                        return new JSONObject(u);
                    }else{
                        throw new WrongPasswordException("password sbagliata");
                    }
                }else{
                    throw new UsernameAlreadyExistException("username non esistente");
                }
            }else{
                System.out.println("Connessione chiusa!");
            }
        }catch (SQLException e){
            System.out.println(e.toString());
        }

        return r.put("Errore", "qualcosa è andato storto");
    }


    /**
     *
     * @param rs risultato della query
     * @return mappa di stringhe (simile ad un array associativo [php])
     * @throws SQLException
     */
    private static HashMap<String, String> createRowObj(ResultSet rs) throws SQLException {
        HashMap<String, String> map = new HashMap<String, String>();

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        for (int i = 1; i <= columnsNumber; i++) {
            map.put(rsmd.getColumnName(i), rs.getString(i));
        }

        return map;
    }



}