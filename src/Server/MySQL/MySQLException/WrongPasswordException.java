package Server.MySQL.MySQLException;

import java.sql.SQLException;

public class WrongPasswordException extends SQLException{
    public WrongPasswordException(){
        super();
    }
    public WrongPasswordException(String message) {
        super(message);
    }
}
