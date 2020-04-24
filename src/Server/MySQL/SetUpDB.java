package Server.MySQL;

import java.sql.*;

public class SetUpDB {

    public static void main(String args[]) {

        try {
            MySQL.openConnection();
            //MySQL.authentication("mario", "2390udf0"); //test autenticazine
            MySQL.createTables();
            MySQL.closeConnection();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
