package Client;

import Client.Controllers.LogInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Apre la pagina di log in
 *
 * @author Sebastiano Sartor
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // carica il file fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/LogInView.fxml"));
        // setta il controller della pagina
        loader.setController(new LogInController(primaryStage));
        // apre la finestra
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.requestFocus();
    }
}
