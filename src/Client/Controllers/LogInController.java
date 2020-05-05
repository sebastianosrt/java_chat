package Client.Controllers;

import Client.Models.Autenticazione;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import animatefx.animation.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
* Questa classe gestisce l'interfaccia di log in
* @author Sebastiano Sartor
* */
public class LogInController implements Initializable {
    @FXML private Pane loginPanel;
    @FXML private Stage stage;
    @FXML private Pane registrazionePanel;
    @FXML private TextField username_f;
    @FXML private TextField password_f;
    @FXML private Label error_f;
    @FXML private Label response;
    @FXML private Button submit_f;
    @FXML private Button registrati_f;
    @FXML private TextField username_r;
    @FXML private TextField password_r;
    @FXML private Label error_r;
    @FXML private Button submit_r;
    @FXML private Button login_r;
    @FXML private Label registrati_label;
    @FXML private Label login_label;
    @FXML private ImageView closeBtn;
    @FXML private ImageView minimizeBtn;
    @FXML private ImageView closeBtn1;
    @FXML private ImageView minimizeBtn1;
    private double xOffset = 0;
    private double yOffset = 0;

    public LogInController(Stage stage) { this.stage = stage; }

    /**
    * Questo metodo prende le credenziali dalla view, esegue la query al database in caso di errore lo mostra, altrimenti apre l'interfaccia della chat
    * @param event - l'evento del click di tipo MouseEvent
    * */
    public void logIn(MouseEvent event) {
        // prende le credenziali dalle caselle di testo
        String username = username_f.getText();
        String password = password_f.getText();
        String error;
        if (username.length() == 0) {
            error_f.setText("Inserisci un'username");
            return;
        }
        if (password.length() == 0) {
            error_f.setText("Inserisci una password");
            return;
        }
        error = Autenticazione.login(username, password);
        if (error.length() > 0) {
            error_f.setText(error);
            return;
        }
        // apre la chat
        try {
            apriChatView(username, event);
        } catch (IOException e) {
            error_f.setText("Errore interno\nImpossible aprire la chat");
        }
    }

    /**
     * Questo metodo registra un utente
     */
    public void signUp() {
        response.setText("");
        // prende le credenziali dalle caselle di testo
        String username = username_r.getText();
        String password = password_r.getText();
        String error;
        if (username.length() == 0) {
            error_r.setText("Inserisci un'username");
            return;
        }
        if (password.length() == 0) {
            error_r.setText("Inserisci una password");
            return;
        }
        error = Autenticazione.signup(username, password);
        if (error.length() > 0)
            error_r.setText(error);
        else {
            response.setText("Registrato correttamente!");
            password_r.setText("");
            username_r.setText("");
        }
    }

    /**
    * Questo metodo apre la chat
    * @param username - l'username dell'utente che ha aperto la chat
    * @param event - l'oggetto dell'evento click
    * @throws IOException
    * */
    private void apriChatView(String username, MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/Views/ChatView.fxml"));
        loader.setController(new ClientController(stage));
        ClientController controller = loader.getController();
        controller.setUsername(username);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.close();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.requestFocus();
        node.setFocusTraversable(true);
    }

    /**
    * Questo metodo gestisce i click del mouse
    * @param event - l'oggetto dell'evento click
    * */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        error_r.setText("");
        error_f.setText("");
        if (event.getSource() == submit_f) {
            logIn(event);
        } else if (event.getSource() == submit_r) {
            signUp();
        } else if (event.getSource() == registrati_label || event.getSource() == registrati_f) {
            new Pulse(registrazionePanel).play();
            registrazionePanel.toFront();
        } else if (event.getSource() == login_label || event.getSource() == login_r) {
            new Pulse(loginPanel).play();
            loginPanel.toFront();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        error_f.setText("");
        error_r.setText("");
        response.setText("");

        // chiudi scheda
        closeBtn.setOnMouseClicked(e -> Platform.exit());
        closeBtn1.setOnMouseClicked(e -> Platform.exit());

        // minimizza scheda
        minimizeBtn.setOnMouseClicked(e -> ((Stage)((ImageView)e.getSource()).getScene().getWindow()).setIconified(true));
        minimizeBtn1.setOnMouseClicked(e -> ((Stage)((ImageView)e.getSource()).getScene().getWindow()).setIconified(true));

        // muove la finestra quando viene trascinata
        loginPanel.setOnMousePressed(event12 -> {
            xOffset = event12.getSceneX();
            yOffset = event12.getSceneY();
        });
        loginPanel.setOnMouseDragged(event1 -> {
            stage.setX(event1.getScreenX() - xOffset);
            stage.setY(event1.getScreenY() - yOffset);
        });
        registrazionePanel.setOnMousePressed(event12 -> {
            xOffset = event12.getSceneX();
            yOffset = event12.getSceneY();
        });
        registrazionePanel.setOnMouseDragged(event1 -> {
            stage.setX(event1.getScreenX() - xOffset);
            stage.setY(event1.getScreenY() - yOffset);
        });
    }
}
