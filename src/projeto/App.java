package projeto;

import java.io.BufferedReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.T;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Criação da janela principal do aplicativo
        
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Server 1"); // Define o título da janela
        
        // Configuração da cena usando um arquivo FXML chamado "primary.fxml"
        try {
            scene = new Scene(loadFXML("primary"), 400, 400); // Carrega o arquivo FXML
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        primaryStage.setScene(scene); // Define a cena na janela
        primaryStage.show(); // Mostra a janela
    }

    // Define a raiz da cena para o arquivo FXML especificado
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Carrega o arquivo FXML   
    private static Parent loadFXML(String fxml) throws IOException {
        // Cria um objeto FXMLLoader para carregar o arquivo FXML
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load(); // Carrega o arquivo FXML
    }

    public static void main(String[] args) {
        launch();
    }
}
