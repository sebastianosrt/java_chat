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
 * @author Sebastiano Sartor
 */
public class Main extends Application {
    // apre la pagina di log in
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/LogInView.fxml"));
        loader.setController(new LogInController(primaryStage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
