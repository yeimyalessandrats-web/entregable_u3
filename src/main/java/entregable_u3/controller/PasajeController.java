package entregable_u3.controller;

import entregable_u3.HelloApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entregable_u3.implementacion.PasajeDaoImpl;
import entregable_u3.interfaces.IPasajeDao;
import entregable_u3.modelo.Pasaje;
import entregable_u3.utils.UtilAlerts;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PasajeController {
    private static UtilAlerts utilAlert = new UtilAlerts();
    // fecha y hora que se muestran en la tabla
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private TableView<Pasaje> tablePasaje;
    @FXML
    private TextField txtBuscarDni;
    @FXML
    private TableColumn<Pasaje, Number> ColNumero;
    @FXML
    private TableColumn<Pasaje, Integer> ColId;
    @FXML
    private TableColumn<Pasaje, String> ColCodigo;
    @FXML
    private TableColumn<Pasaje, String> ColEmision;
    @FXML
    private TableColumn<Pasaje, String> ColPasajero;
    @FXML
    private TableColumn<Pasaje, String> ColDni;
    @FXML
    private TableColumn<Pasaje, String> ColComprador;
    @FXML
    private TableColumn<Pasaje, String> ColViaje;
    @FXML
    private TableColumn<Pasaje, String> ColSalida;
    @FXML
    private TableColumn<Pasaje, Double> ColCosto;

    private final IPasajeDao pasajeDao = new PasajeDaoImpl();
    private ObservableList<Pasaje> listaPasaje;

    @FXML
    public void initialize() {
        ColNumero.setCellFactory(colum -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        ColId.setCellValueFactory(
                cd -> cd.getValue().idpasajeProperty().asObject());
        // codigo del pasaje que se genera automaticamente
        ColCodigo.setCellValueFactory(
                cd -> cd.getValue().codigoProperty());
        ColEmision.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getFechaEmision().format(FMT)));
        // el pasaje llama al pasajero y al viaje
        ColPasajero.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getPasajero().getNombres() + " "
                        + cd.getValue().getPasajero().getApellidos()));
        ColDni.setCellValueFactory(
                cd -> cd.getValue().getPasajero().dniProperty());
        // quien compro y pago el pasaje (puede ser distinto al pasajero que viaja)
        ColComprador.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getComprador().getNombres() + " "
                        + cd.getValue().getComprador().getApellidos()));
        ColViaje.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getViaje().getTarifa().getOrigen() + " -> "
                        + cd.getValue().getViaje().getTarifa().getDestino()));
        ColSalida.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getViaje().getFechaSalida().format(FMT)));
        ColCosto.setCellValueFactory(
                cd -> cd.getValue().costoProperty().asObject());
        cargarDatos();
    }

    private void cargarDatos() {
        listaPasaje = FXCollections.observableArrayList(pasajeDao.listAll());
        tablePasaje.setItems(listaPasaje);
    }

    // buscador por dni: filtra la tabla mientras se escribe
    @FXML
    private void buscarPorDni() {
        String dni = txtBuscarDni.getText().trim();
        if (dni.isEmpty()) {
            cargarDatos();
        } else {
            listaPasaje = FXCollections.observableArrayList(pasajeDao.searchByDni(dni));
            tablePasaje.setItems(listaPasaje);
        }
    }

    @FXML
    private void handleDelete() {
        Pasaje selectId = tablePasaje.getSelectionModel().getSelectedItem();
        if (selectId != null) {
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar el pasaje " + selectId.getCodigo() + "?",
                    "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (pasajeDao.delete(selectId)) {
                    listaPasaje.remove(selectId);
                } else {
                    Alert error = utilAlert.mostrarAlerts("Error",
                            "No se pudo eliminar el pasaje", "ERROR");
                    error.show();
                }
            }
        } else {
            Alert warning = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un pasaje para eliminar", "WARNING");
            warning.showAndWait();
        }
    }

    @FXML
    private void handleNew() {
        showForm();
    }

    // formulario de compra: el pasajero elige los viajes y la cantidad de pasajes
    private void showForm() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/entregable_u3/views/pasaje-form.fxml"));
            Parent root = loader.load();
            PasajeFormController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Pasaje");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                List<Pasaje> compra = controller.getCompra();
                boolean result = pasajeDao.insertAll(compra);
                if (result) {
                    // se muestran los codigos generados y el total pagado
                    double total = 0;
                    for (Pasaje p : compra) {
                        total += p.getCosto();
                    }
                    String codigos = compra.size() == 1
                            ? compra.get(0).getCodigo()
                            : compra.get(0).getCodigo() + " ... " + compra.get(compra.size() - 1).getCodigo();
                    Alert alert = utilAlert.mostrarAlerts("Pasajes registrados",
                            "Registro satisfactorio!!!\nComprador: " + compra.get(0).getComprador().getNombres() + " "
                                    + compra.get(0).getComprador().getApellidos()
                                    + "\nPasajes comprados: " + compra.size()
                                    + "\nCodigos: " + codigos
                                    + "\nTotal pagado: S/ " + String.format("%.2f", total), "CONFIRMATION");
                    alert.show();
                } else {
                    Alert alert = utilAlert.mostrarAlerts("Error", "Error al insertar!!!", "ERROR");
                    alert.show();
                }
                txtBuscarDni.clear();
                cargarDatos();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
