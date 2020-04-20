package Client.Controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import animatefx.animation.*;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


/*
 * Questa classe gestisce l'interfaccia di log in
 * @author Sebastiano Sartor
 * */
public class ClientController implements Initializable {
    private String username;
    @FXML private Label username_f;
    @FXML private Label destinatario_f;
    @FXML private TextField search_f;
    @FXML private TextField message_f;
    @FXML private Button sendBtn;
    @FXML private ScrollPane scrollPane;
    private VBox chatContaier;
    private int lastId = 0;
    private ContextMenu menu;
    private MenuItem copia;
    private MenuItem elimina;
    private HBox selectedItem;

    /*
     * Questo metodo gestisce i click del mouse
     * */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        // invia il messaggio
        if (event.getSource() == sendBtn) {
            invia("", "wefewvwev");
        }
    }

    /*
     * Questo metodo inizializza l'interfaccia
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username_f.setText(username);
        username_f.setFocusTraversable(true);
        username_f.requestFocus();

        chatContaier = new VBox();
        chatContaier.setPrefWidth(scrollPane.getPrefWidth() - 20);
        scrollPane.setContent(chatContaier);

        chatContaier.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));

        menu = new ContextMenu();
        elimina = new MenuItem("elimina");
        copia = new MenuItem("copia");
        menu.getItems().addAll(elimina, copia);

        elimina.setOnAction(event -> {
            String id = selectedItem.getId();
            chatContaier.getChildren().remove(selectedItem);
        });
        copia.setOnAction(event -> {
            TextFlow f = (TextFlow) selectedItem.getChildren().get(0);
            String str = f.getAccessibleText();
            // copy on clipboard
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str), null);
        });
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void invia(String username, String message) {
        message = Double.toString(Math.random());

        Text text=new Text(message);
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
        hbox.setId(lastId++ + "");

        flow.setAccessibleText(message);

        hbox.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                selectedItem = (HBox) e.getSource();
                menu.show(scrollPane, e.getScreenX(), e.getScreenY());
            }
        });

        chatContaier.getChildren().addAll(hbox);
    }
}
