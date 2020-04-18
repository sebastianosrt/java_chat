package Client.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
public class LogIn implements Initializable {
    @FXML private Pane loginPanel;
    @FXML private Pane registrazionePanel;
    @FXML private TextField username_f;
    @FXML private TextField password_f;
    @FXML private Label error_f;
    @FXML private Button submit_f;
    @FXML private Button registrati_f;
    @FXML private TextField username_r;
    @FXML private TextField password_r;
    @FXML private Label error_r;
    @FXML private Button submit_r;
    @FXML private Button login_r;
    @FXML private Label registrati_label;
    @FXML private Label login_label;

    /*
    * Questo metodo prende le credenziali dalla view, esegue la query al database
    * in caso di errore lo mostra, altrimenti apre l'interfaccia della chat
    * @params event - l'evento del click di tipo MouseEvent
    * */
    public void logIn(MouseEvent event) {
        // prende le credenziali dalle caselle di testo
        String username = username_f.getText();
        String password = username_f.getText();
        // TODO: query
        String error = "";
//        if (error.length() > 0)
//            error_f.setText(error);
//        // apre la chat
//        else {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/Client/Views/ChatView.fxml")));
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }

    /*
    * Questo metodo gestisce i click del mouse
    * */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        if (event.getSource() == submit_f) {
            logIn(event);
        } else if (event.getSource() == submit_r) {
//            signup(event);
        } else if (event.getSource() == registrati_label || event.getSource() == registrati_f) {
            new Pulse(registrazionePanel).play();
            registrazionePanel.toFront();
        } else if (event.getSource() == login_label || event.getSource() == login_r) {
            new Pulse(loginPanel).play();
            loginPanel.toFront();
        }
    }

    /*
    * Questo metodo inizializza l'interfaccia
    * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        error_f.setText("");
        error_r.setText("");
    }
}