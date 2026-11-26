package entregable_u3.controller;

import entregable_u3.implementacion.PasajeroDaoImpl;
import entregable_u3.interfaces.IPasajeroDao;
import entregable_u3.modelo.Pasajero;
import entregable_u3.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Cuando se compran varios pasajes, aqui se indica quienes son los otros pasajeros
// (se buscan por DNI entre los pasajeros registrados)
public class AcompananteFormController {
    private static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblInfo;
    @FXML
    private VBox vbFilas;

    private final IPasajeroDao pasajeroDao = new PasajeroDaoImpl();

    private TextField[] txtDni;
    private Label[] lblNombre;
    private Pasajero[] encontrados;
    private Pasajero comprador;
    private final List<Pasajero> acompanantes = new ArrayList<>();
    private boolean saved = false;

    // cantidad = cuantos pasajeros faltan por indicar; viaje = texto del viaje que se esta comprando
    public void setDatos(int cantidad, String viaje, Pasajero comprador) {
        this.comprador = comprador;
        lblInfo.setText("Viaje: " + viaje + "\nComprador: " + comprador.getNombres() + " "
                + comprador.getApellidos() + " (viaja en este viaje)\n"
                + "Indique el DNI de los otros " + cantidad + " pasajero(s):");
        txtDni = new TextField[cantidad];
        lblNombre = new Label[cantidad];
        encontrados = new Pasajero[cantidad];
        for (int i = 0; i < cantidad; i++) {
            final int pos = i;
            Label lblNumero = new Label("Pasajero " + (i + 2));
            lblNumero.setMinWidth(80.0);
            txtDni[i] = new TextField();
            txtDni[i].setPromptText("DNI (8 digitos)");
            txtDni[i].setPrefWidth(130.0);
            lblNombre[i] = new Label("Pasajero no encontrado");
            lblNombre[i].setStyle("-fx-text-fill: #8493A3;");
            // al escribir el dni se busca el pasajero
            txtDni[i].setOnKeyReleased(e -> buscar(pos));
            HBox fila = new HBox(10.0, lblNumero, txtDni[i], lblNombre[i]);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            vbFilas.getChildren().add(fila);
        }
    }

    private void buscar(int pos) {
        String dni = txtDni[pos].getText();
        if (dni.length() == 8) {
            encontrados[pos] = pasajeroDao.searchByDni(dni);
            if (encontrados[pos] != null) {
                lblNombre[pos].setText(encontrados[pos].getNombres() + " " + encontrados[pos].getApellidos());
                lblNombre[pos].setStyle("-fx-text-fill: green;");
            } else {
                lblNombre[pos].setText("No encontrado (registrelo en el menu Pasajeros)");
                lblNombre[pos].setStyle("-fx-text-fill: red;");
            }
        } else {
            encontrados[pos] = null;
            lblNombre[pos].setText("Pasajero no encontrado");
            lblNombre[pos].setStyle("-fx-text-fill: #8493A3;");
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public List<Pasajero> getAcompanantes() {
        return acompanantes;
    }

    private void cerrar() {
        ((Stage) vbFilas.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleAceptar() {
        Set<Integer> ids = new HashSet<>();
        ids.add(comprador.getIdpasajero());
        for (int i = 0; i < encontrados.length; i++) {
            if (encontrados[i] == null) {
                Alert alert = utilAlerts.mostrarAlerts("Error",
                        "Falta indicar al pasajero " + (i + 2) + " (busquelo por su DNI)", "ERROR");
                alert.showAndWait();
                return;
            }
            // cada persona ocupa un solo asiento
            if (!ids.add(encontrados[i].getIdpasajero())) {
                Alert alert = utilAlerts.mostrarAlerts("Pasajero repetido",
                        encontrados[i].getNombres() + " " + encontrados[i].getApellidos()
                                + " ya esta en la compra. Cada pasajero ocupa un solo asiento", "WARNING");
                alert.showAndWait();
                return;
            }
        }
        for (Pasajero p : encontrados) {
            acompanantes.add(p);
        }
        saved = true;
        cerrar();
    }
}
