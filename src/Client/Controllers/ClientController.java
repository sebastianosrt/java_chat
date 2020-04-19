package Client.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import animatefx.animation.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/*
 * Questa classe gestisce l'interfaccia di log in
 * @author Sebastiano Sartor
 * */
public class ClientController implements Initializable {
    private String username;
    @FXML Label username_f;
    @FXML Label destinatario_f;
    @FXML TextField search_f;
    @FXML TextField message_f;
    @FXML Button sendBtn;

    /*
     * Questo metodo gestisce i click del mouse
     * */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        // invia il messaggio
        if (event.getSource() == sendBtn) {
//            inviaMessaggio(message_f.getText());
        }
    }

    /*
     * Questo metodo inizializza l'interfaccia
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username_f.setText(username);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
