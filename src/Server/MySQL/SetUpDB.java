package Server.MySQL;

public class SetUpDB {
    public static void main(String[] args) {
        MySQL.openConnection();
        MySQL.createTables();
        MySQL.closeConnection();
    }
}
