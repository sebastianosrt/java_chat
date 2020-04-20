package Client.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML ScrollPane scrollPane;
    VBox chatContaier;

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
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void invia(String username, String message) {
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
            hbox.getChildren().add(flow);
        } else {
            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(flow);
        }

        hbox.getStyleClass().add("hbox");
        chatContaier.getChildren().addAll(hbox);
    }
}
