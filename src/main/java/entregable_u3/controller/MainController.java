package entregable_u3.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import entregable_u3.modelo.UserSession;


import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblUsuario;

    @FXML
    public void initialize(){
        if (UserSession.getInstance().getUserLogin() !=null){
            lblUsuario.setText("Bienvenido, " +
                    UserSession.getInstance().getUserLogin().getNombres());
        }
        showHome();
    }
    @FXML
    private  void showHome(){
        loadView("home-view.fxml");
    }
    @FXML
    private void showConductor(){
        loadView("conductor-view.fxml");
    }
    @FXML
    private void showPasajero(){
        loadView("pasajero-view.fxml");
    }
    @FXML
    private void showVehiculo(){
        loadView("vehiculo-view.fxml");
    }
    @FXML
    private void showTarifa(){
        loadView("tarifa-view.fxml");
    }
    @FXML
    private void showViaje(){
        loadView("viaje-view.fxml");
    }
    @FXML
    private void showPasaje(){
        loadView("pasaje-view.fxml");
    }
    @FXML
    private void handleLogout(){
        UserSession.getInstance().cleanSession();
        System.exit(0);
    }
    //metodo que llama y busca a las vistas
    private void loadView(String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/entregable_u3/views/" +fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error de carga de vista"+e.getMessage());
        }
    }

}
