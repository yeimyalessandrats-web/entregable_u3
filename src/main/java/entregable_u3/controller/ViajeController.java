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
import entregable_u3.implementacion.ViajeDaoImpl;
import entregable_u3.interfaces.IViajeDao;
import entregable_u3.modelo.Viaje;
import entregable_u3.utils.UtilAlerts;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ViajeController {
    private static UtilAlerts utilAlert = new UtilAlerts();
    // fecha y hora que se muestran en la tabla
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private TableView<Viaje> tableViaje;
    @FXML
    private TableColumn<Viaje, Number> ColNumero;
    @FXML
    private TableColumn<Viaje, Integer> ColId;
    @FXML
    private TableColumn<Viaje, String> ColOrigen;
    @FXML
    private TableColumn<Viaje, String> ColDestino;
    @FXML
    private TableColumn<Viaje, String> ColSalida;
    @FXML
    private TableColumn<Viaje, String> ColLlegada;
    @FXML
    private TableColumn<Viaje, String> ColConductor;
    @FXML
    private TableColumn<Viaje, String> ColVehiculo;
    @FXML
    private TableColumn<Viaje, Double> ColPrecio;

    private final IViajeDao viajeDao = new ViajeDaoImpl();
    private ObservableList<Viaje> listaViaje;

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
                cd -> cd.getValue().idviajeProperty().asObject());
        // el viaje llama a la tarifa, al conductor y al vehiculo
        ColOrigen.setCellValueFactory(
                cd -> cd.getValue().getTarifa().origenProperty());
        ColDestino.setCellValueFactory(
                cd -> cd.getValue().getTarifa().destinoProperty());
        ColSalida.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getFechaSalida().format(FMT)));
        ColLlegada.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getFechaLlegada().format(FMT)));
        ColConductor.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getConductor().getNombres() + " "
                        + cd.getValue().getConductor().getApellidos()));
        ColVehiculo.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getVehiculo().toString()));
        ColPrecio.setCellValueFactory(
                cd -> cd.getValue().getTarifa().precioProperty().asObject());
        cargarDatos();
    }

    private void cargarDatos() {
        listaViaje = FXCollections.observableArrayList(viajeDao.listAll());
        tableViaje.setItems(listaViaje);
    }

    @FXML
    private void handleDelete() {
        Viaje selectId = tableViaje.getSelectionModel().getSelectedItem();
        if (selectId != null) {
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar el viaje " + selectId + "?",
                    "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (viajeDao.delete(selectId)) {
                    listaViaje.remove(selectId);
                } else {
                    Alert error = utilAlert.mostrarAlerts("Error",
                            "No se pudo eliminar, el viaje tiene pasajes vendidos", "ERROR");
                    error.show();
                }
            }
        } else {
            Alert warning = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un viaje para eliminar", "WARNING");
            warning.showAndWait();
        }
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Viaje selectViaje = tableViaje.getSelectionModel().getSelectedItem();
        if (selectViaje != null) {
            Viaje viajeFull = viajeDao.searchById(selectViaje.getIdviaje());
            showForm(viajeFull);
        } else {
            Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un viaje de la tabla", "WARNING");
            alert.show();
        }
    }

    private void showForm(Viaje v) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/entregable_u3/views/viaje-form.fxml"));
            Parent root = loader.load();
            ViajeFormController controller = loader.getController();
            controller.setViaje(v); //pasamos el viaje null si es nuevo
            Stage stage = new Stage();
            stage.setTitle(v == null ? "Nuevo Viaje" : "Editar Viaje");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                Viaje viajeProcesado = controller.getViaje();
                if (v == null) {
                    boolean result = viajeDao.insert(viajeProcesado);
                    if (result) {
                        Alert alert = utilAlert.mostrarAlerts("Success", "Registro satisfactorio!!!", "CONFIRMATION");
                        alert.show();
                    } else {
                        Alert alert = utilAlert.mostrarAlerts("Error", "Error al insertar!!!", "ERROR");
                        alert.show();
                    }
                } else {
                    boolean result = viajeDao.update(viajeProcesado);
                    if (result) {
                        Alert alert = utilAlert.mostrarAlerts("Update",
                                "Registro actualizado!!!", "WARNING");
                        alert.showAndWait();
                    } else {
                        Alert alert = utilAlert.mostrarAlerts("Error",
                                "Error al actualizar!!!", "ERROR");
                        alert.show();
                    }
                }
                cargarDatos();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
