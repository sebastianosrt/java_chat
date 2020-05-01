package Client.Controllers;

import Client.Models.Client;
import Client.Models.Messaggio;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Questa classe gestisce l'interfaccia di log in
 * @author Sebastiano Sartor
 * */
public class ClientController implements Initializable {
    private String username;
    private String contattoAttivo;
    private Client client;
    private boolean newContact = false;

    @FXML private AnchorPane body;
    @FXML private Label username_f;
    @FXML private Label destinatario_f;
    @FXML private TextField search_f;
    @FXML private JFXTextArea message_f;
    @FXML private Button sendBtn;
    @FXML private ScrollPane scrollPane;
    @FXML private ScrollPane contactsPane;
    @FXML private Pane coveringPane;
    @FXML private ImageView closeBtn;
    @FXML private ImageView minimizeBtn;
    @FXML private VBox chatContaier;
    @FXML private VBox contactsContaier;
    @FXML private ContextMenu menu;
    @FXML private MenuItem copia;
    @FXML private MenuItem elimina;
    @FXML private HBox selectedMessage;
    @FXML private HBox activeContact = null;

    /**
     * Questo metodo gestisce i click del mouse
     * @param event - evento click
     */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        // invia il messaggio
        if (event.getSource() == sendBtn) {
            addMessaggio(inviaMessaggio(username, message_f.getText()));
            message_f.setText("");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username_f.setText(username);
        username_f.setFocusTraversable(true);
        username_f.requestFocus();

        // chat
        chatContaier = new VBox();
        chatContaier.setPrefWidth(scrollPane.getPrefWidth() - 20);
        scrollPane.setContent(chatContaier);
        chatContaier.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));

        // menù
        menu = new ContextMenu();
        elimina = new MenuItem("elimina");
        copia = new MenuItem("copia");
        menu.getItems().addAll(elimina, copia);

        elimina.setOnAction(event -> {
            String id = selectedMessage.getId();
            chatContaier.getChildren().remove(selectedMessage);
        });
        copia.setOnAction(event -> {
            TextFlow f = (TextFlow) selectedMessage.getChildren().get(0);
            String str = f.getAccessibleText();
            // copy on clipboard
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str), null);
        });

        // contatti
        contactsContaier = new VBox();
        contactsContaier.setPrefWidth(contactsPane.getPrefWidth() - 5);
        contactsPane.setContent(contactsContaier);

        // quando viene premuto enter invia un messaggio
        body.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                addMessaggio(inviaMessaggio(username, message_f.getText()));
                message_f.setText("");
            }
        });
        // chiudi scheda
        closeBtn.setOnMouseClicked(e -> Platform.exit());
        // minimizza scheda
        minimizeBtn.setOnMouseClicked(e -> ((Stage)((ImageView)e.getSource()).getScene().getWindow()).setIconified(true));
        // ricerca utente
        search_f.setOnKeyReleased(e -> {
            if (search_f.getText().length() > 0) caricaContatti(client.searchUsers(search_f.getText()));
            else caricaContatti(client.getContattiFromDataBase());
        });

        client = new Client(username, this);
        new Thread(client).start();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Questo metodo invia il messaggio al destinatario
     * @param username - l'username del mittente del messaggio
     * @param message - il contenuto del messaggio
     * */
    public Messaggio inviaMessaggio(String username, String message) {
        Messaggio m = new Messaggio(0, message, username, contattoAttivo, "text");
        if (username.equals(contattoAttivo) || username.equals(this.username)) {
            if (message.length() > 0) {
                //toglie gli \n finali
                while (message.length() > 0 && message.charAt(message.length() - 1) == '\n') message = message.substring(0, message.length() - 1);
                if (message.length() > 0) {
                    m.id = client.inviaMessaggio(this.username, message);
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * Questo metodo aggiunge alla view un nuovo messaggio e gli aggiunge un event listener per il tasto destro del mouse
     * @param message - il messaggio
     * */
    public void addMessaggio(Messaggio message) {
        if (message != null) {
            String username = message.mittente;
            String testo = message.testo;
            if (username.equals(contattoAttivo) || username.equals(this.username)) {
                if (testo.length() > 0) {
                    if (newContact) {
                        client.addContactToDataBase(contattoAttivo);
                        newContact = false;
                        caricaContatti(client.getContattiFromDataBase());
                    }
                    //toglie gli \n finali
                    while (testo.length() > 0 && testo.charAt(testo.length() - 1) == '\n') testo = testo.substring(0, testo.length() - 1);
                    if (testo.length() > 0) {
                        int id = message.id;
                        Text text=new Text(testo);
                        text.setFill(Color.BLACK);
                        TextFlow tempFlow = new TextFlow();
                        if(!this.username.equals(username)){
                            text.setFill(Color.WHITE);
                            Text txtName=new Text(username + "\n");
                            txtName.setFill(Color.WHITE);
                            txtName.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                            txtName.getStyleClass().add("txtName");
                            tempFlow.getChildren().add(txtName);
                        }
                        tempFlow.getChildren().add(text);
                        tempFlow.setMaxWidth(200);
                        TextFlow flow = new TextFlow(tempFlow);
                        HBox hbox = new HBox(12);
                        if (!this.username.equals(username)) {
                            tempFlow.getStyleClass().add("tempFlowFlipped");
                            flow.getStyleClass().add("textFlowFlipped");
                            chatContaier.setAlignment(Pos.TOP_LEFT);
                            hbox.setAlignment(Pos.CENTER_LEFT);
                        } else {
                            tempFlow.getStyleClass().add("tempFlow");
                            flow.getStyleClass().add("textFlow");
                            hbox.setAlignment(Pos.BOTTOM_RIGHT);
                        }
                        hbox.getChildren().add(flow);
                        hbox.getStyleClass().add("hbox");
                        hbox.setId(id + "");
                        flow.setAccessibleText(testo);
                        // seleziona messaggio
                        hbox.setOnMouseClicked((MouseEvent e) -> {
                            if (e.getButton() == MouseButton.SECONDARY) {
                                selectedMessage = (HBox) e.getSource();
                                menu.show(scrollPane, e.getScreenX(), e.getScreenY());
                            }
                        });
                        chatContaier.getChildren().addAll(hbox);
                        raiseContact();
                    }
                }
            }
        }
    }

    /**
     * Questo metodo mostra i messaggi con un contatto
     * @param messaggi - array contenente i messaggi della chat messaggi
     */
    public void caricaMessaggi(ArrayList<Messaggio> messaggi) {
        if (client.getContattiFromDataBase().contains(contattoAttivo)) {
            chatContaier.getChildren().clear();
            messaggi.forEach(m -> System.out.println(m.id));
//            messaggi.forEach(m -> inviaMessaggio(m.mittente, m.testo));
        } else newContact = true;
    }

    /**
     * Questo metodo aggiunge un contatto nella view
     * @param username - l'username del contatto da aggiungere
     */
    public void addContatto(String username) {
        HBox hbox = new HBox();
        hbox.getStyleClass().addAll("hbox", "contact");
        hbox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(username);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hbox.getChildren().addAll(label);
        hbox.setAccessibleText(label.getText());

        hbox.setOnMouseClicked(this::selectContact);
        contactsContaier.getChildren().addAll(hbox, new Separator());
    }

    /**
     * Questo metodo carica tutti i contatti dell'utente nella view
     * @param contatti - array contenente i contatti dell'utente
     */
    public void caricaContatti(ArrayList<String> contatti) {
        contactsContaier.getChildren().clear();
        contatti.forEach(this::addContatto);
    }

    /**
     * Questo metodo seleziona un contatto
     * @param e - l'evento del click di tipo MouseEvent
     * */
    private void selectContact(MouseEvent e) {
        // prendo il contatto selezionato dall'evento
        HBox b = (HBox) e.getSource();
        // se il contatto selezionato non è già attivo
        if (b != activeContact) {
            // toglie la classe css attivo dal contatto attivo precendente
            if (activeContact != null) activeContact.getStyleClass().remove("contactActive");
            // rende attivo il nuovo contatto
            activeContact = b;
            b.getStyleClass().add("contactActive");
            // toglie il pannello che dice che nessuna chat è selezionata
            coveringPane.toBack();
            // toglie tutti i messaggi con il contatto precedente
            chatContaier.getChildren().clear();
            // setta il nome del destinatario
            destinatario_f.setText(b.getAccessibleText());
            contattoAttivo = b.getAccessibleText();
            // resetta la ricerca
            search_f.setText("");
            client.setContatto_attivo(contattoAttivo);
//            caricaContatti(client.getContattiFromDataBase());
            caricaMessaggi(client.getMessaggiFromDataBase(contattoAttivo));
        }
    }

    /**
     * Questo metodo porta in cima alla lista dei contatti l'ultimo con cui si è messaggato se non è già primo
     * */
    private void raiseContact() {
        if (!newContact) {
            // metto i contatti in una lista
            List<Node> nodes = new ArrayList<>(contactsContaier.getChildren());
            // se il contatto è già in cima
            if (nodes.get(0).equals(activeContact))
                return;
            // prendo l'indice del contatto da portare in alto per rimuovere il separator
            int i = nodes.indexOf(activeContact);
            // rimuovo separator
            nodes.remove(i + 1);
            // rimuovo il contatto da portare in alto
            nodes.remove(activeContact);
            // lo porto in alto (lo metto come primo nodo della lista)
            nodes.add(0, activeContact);
            // aggiungo un separator in 2 posizione
            nodes.add(1, new Separator());
            // rimuovo tutti i contatti e ci metto la nuova lista
            contactsContaier.getChildren().clear();
            contactsContaier.getChildren().addAll(nodes);
        }
    }
}
