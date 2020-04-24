package Server.MySQL.MySQLException;

import java.sql.SQLException;

public class UsernameAlreadyExistException extends SQLException {
    public UsernameAlreadyExistException(){
        super();
    }
    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
