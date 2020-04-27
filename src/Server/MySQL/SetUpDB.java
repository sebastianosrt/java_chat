package Server.MySQL;

import java.sql.*;

public class SetUpDB {

    public static void main(String args[]) {

        try {
            MySQL.openConnection();
            //MySQL.authentication("mario", "2390udf0"); //test autenticazine
            MySQL.createTables();
            //MySQL.addUser("admin", "admin");
            //System.out.println(MySQL.addContact("admin", "paperino")); //aggiungi contatto
            System.out.println(MySQL.getListContacts("mario")); //aggiungi contatto
            System.out.println(MySQL.searchUser("d")); //cerca username

            MySQL.closeConnection();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
