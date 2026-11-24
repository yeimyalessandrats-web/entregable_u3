package entregable_u3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class
                .getResource("/entregable_u3/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        // Asignación de ícono a la ventana principal
        stage.getIcons().add(new
        Image(getClass().getResourceAsStream
                ("/entregable_u3/img/logo.jpg")));
        stage.setTitle("Login - Sistema de Transporte!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
