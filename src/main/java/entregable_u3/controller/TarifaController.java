package entregable_u3.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entregable_u3.HelloApplication;
import entregable_u3.implementacion.TarifaDaoImpl;
import entregable_u3.interfaces.ITarifaDao;
import entregable_u3.modelo.Tarifa;
import entregable_u3.utils.UtilAlerts;

import java.time.LocalTime;
import java.util.Optional;

public class TarifaController {
    private static UtilAlerts utilAlert = new UtilAlerts();

    //definimos la tabla
    @FXML
    private TableView<Tarifa> tableTarifa;
    //variable correlativa
    @FXML
    private TableColumn<Tarifa,Number> ColNumero;
    @FXML
    private TableColumn<Tarifa,Integer> ColId;
    @FXML
    private TableColumn<Tarifa,String> ColOrigen;
    @FXML
    private TableColumn<Tarifa,String> ColDestino;
    @FXML
    private TableColumn<Tarifa,LocalTime> ColHora;
    @FXML
    private TableColumn<Tarifa,Integer> ColDuracion;
    @FXML
    private TableColumn<Tarifa,Double> ColPrecio;

    private final ITarifaDao tarifaDao = new TarifaDaoImpl();
    private ObservableList<Tarifa> listaTarifa;


    @FXML
    public void initialize(){
        ColNumero.setCellFactory(colum -> new TableCell<>(){
            @Override
            protected void updateItem(Number item, boolean empty){
                super.updateItem(item,empty);
                if (empty){
                    setText(null);
                }else{
                    setText(String.valueOf(getIndex() +1));
                }
            }
        });

        ColId.setCellValueFactory(
                cellData -> cellData.getValue().idtarifaProperty().asObject());
        ColOrigen.setCellValueFactory(
                cellData -> cellData.getValue().origenProperty());
        ColDestino.setCellValueFactory(
                cellData -> cellData.getValue().destinoProperty());
        ColHora.setCellValueFactory(
                cellData -> cellData.getValue().horaSalidaProperty());
        ColDuracion.setCellValueFactory(
                cellData -> cellData.getValue().duracionMinProperty().asObject());
        ColPrecio.setCellValueFactory(
                cellData -> cellData.getValue().precioProperty().asObject());
        //llamar al metodo
        cargarDatos();
    }
    private void cargarDatos(){
        listaTarifa = FXCollections.observableArrayList(tarifaDao.listAll());
        tableTarifa.setItems(listaTarifa);
    }
    @FXML
    private void handleDelete(){
        Tarifa selectId = tableTarifa.getSelectionModel().getSelectedItem();
        if (selectId !=null){
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar la tarifa "+selectId.getOrigen()+" -> "
                            +selectId.getDestino()+" de las "+selectId.getHoraSalida()+"?",
                    "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                if (tarifaDao.delete(selectId)){
                    listaTarifa.remove(selectId);
                }else{
                    Alert error = utilAlert.mostrarAlerts("Error",
                            "No se pudo eliminar, la tarifa tiene viajes registrados","ERROR");
                    error.show();
                }
            }
        }else{
            Alert warning = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una tarifa para eliminar","WARNING");
            warning.showAndWait();
        }
    }

    @FXML
    private void handleNew(){
        showForm(null);
    }
    @FXML
    private void handleEdit(){
        Tarifa selectTarifa = tableTarifa.getSelectionModel().getSelectedItem();
        if (selectTarifa !=null){
            Tarifa tarifaFull = tarifaDao.searchById(selectTarifa.getIdtarifa());
            showForm(tarifaFull);
        }else{
            Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una tarifa de la tabla","WARNING");
            alert.show();
        }
    }
    private void showForm(Tarifa t){
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/entregable_u2/views/tarifa-form.fxml"));
            Parent root = loader.load();
            TarifaFormController controller = loader.getController();
            controller.setTarifa(t); //pasamos la tarifa null si es nueva
            Stage stage = new Stage();
            stage.setTitle(t==null ? "Nueva Tarifa": "Editar Tarifa");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene( new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()){
                Tarifa tarifaProcesada = controller.getTarifa();
                if (t == null){
                    boolean result =  tarifaDao.insert(tarifaProcesada);
                    if (result){
                        Alert alert = utilAlert.mostrarAlerts("Success","Registro satisfactorio!!!","CONFIRMATION");
                        alert.show();
                    }else{
                        Alert alert = utilAlert.mostrarAlerts("Error",
                                "Error al insertar (la tarifa ya existe?)","ERROR");
                        alert.show();
                    }
                }else{
                    boolean result =  tarifaDao.update(tarifaProcesada);
                    if (result){
                        Alert alert = utilAlert.mostrarAlerts("Update",
                                "Registro actualizado!!!","WARNING");
                        alert.showAndWait();
                    }else{
                        Alert alert = utilAlert.mostrarAlerts("Error",
                                "Error al actualizar (la tarifa ya existe?)","ERROR");
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
