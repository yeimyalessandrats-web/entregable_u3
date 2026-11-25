package entregable_u3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import entregable_u3.implementacion.ConductorDaoImpl;
import entregable_u3.implementacion.TarifaDaoImpl;
import entregable_u3.implementacion.VehiculoDaoImpl;
import entregable_u3.implementacion.ViajeDaoImpl;
import entregable_u3.interfaces.IViajeDao;
import entregable_u3.modelo.Conductor;
import entregable_u3.modelo.Tarifa;
import entregable_u3.modelo.Vehiculo;
import entregable_u3.modelo.Viaje;
import entregable_u3.utils.UtilAlerts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViajeFormController {
    private static UtilAlerts utilAlerts = new UtilAlerts();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblTitulo;
    // el viaje llama a la tarifa (destino, horario y precio), al conductor y al vehiculo
    @FXML
    private ComboBox<Tarifa> cbTarifa;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private Label lblSalida, lblLlegada, lblPrecio;
    @FXML
    private ComboBox<Conductor> cbConductor;
    @FXML
    private ComboBox<Vehiculo> cbVehiculo;

    private final IViajeDao viajeDao = new ViajeDaoImpl();
    private Viaje viaje;
    private boolean saved = false;

    @FXML
    public void initialize(){
        cbTarifa.getItems().addAll(new TarifaDaoImpl().listAll());
        cbConductor.getItems().addAll(new ConductorDaoImpl().listAll());
        cbVehiculo.getItems().addAll(new VehiculoDaoImpl().listAll());
        dpFecha.setValue(LocalDate.now());
        // la hora de salida y de llegada se calculan con la tarifa y la fecha elegida
        cbTarifa.valueProperty().addListener((ov,oldValue,newValue)->{
            calcularHorario();
        });
        dpFecha.valueProperty().addListener((ov,oldValue,newValue)->{
            calcularHorario();
        });
    }

    private LocalDateTime salida(){
        if (cbTarifa.getValue() == null || dpFecha.getValue() == null){
            return null;
        }
        return LocalDateTime.of(dpFecha.getValue(), cbTarifa.getValue().getHoraSalida());
    }
    private LocalDateTime llegada(){
        LocalDateTime salida = salida();
        if (salida == null){
            return null;
        }
        return salida.plusMinutes(cbTarifa.getValue().getDuracionMin());
    }
    private void calcularHorario(){
        if (salida() != null){
            lblSalida.setText(salida().format(FMT));
            lblLlegada.setText(llegada().format(FMT));
            lblPrecio.setText(String.format("S/ %.2f", cbTarifa.getValue().getPrecio()));
        }else{
            lblSalida.setText("-");
            lblLlegada.setText("-");
            lblPrecio.setText("-");
        }
    }

    public boolean isSaved(){
        return saved;
    }
    public Viaje getViaje(){
        return viaje;
    }
    private boolean validate(){
        if (cbTarifa.getValue() == null || dpFecha.getValue() == null
                || cbConductor.getValue() == null || cbVehiculo.getValue() == null){
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "Todos los campos son obligatorios","WARNING");
            alert.show();
            return false;
        }
        // un viaje nuevo no puede salir en una fecha y hora que ya paso
        if (viaje == null && salida().isBefore(LocalDateTime.now())){
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "La fecha y hora de salida ya paso","WARNING");
            alert.show();
            return false;
        }
        return true;
    }
    private void cerrar(){
        Stage stage = (Stage) lblSalida.getScene().getWindow();
        stage.close();
    }
    //actions of the views
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSave(){
        if (validate()){
            Viaje temp = new Viaje();
            temp.setIdviaje(viaje == null ? 0 : viaje.getIdviaje());
            temp.setTarifa(cbTarifa.getValue());
            temp.setFechaSalida(salida());
            temp.setFechaLlegada(llegada());
            temp.setConductor(cbConductor.getValue());
            temp.setVehiculo(cbVehiculo.getValue());

            // validar que el conductor y el vehiculo no tengan otro viaje en la misma fecha y hora
            if (viajeDao.validateConductor(temp)){
                Alert alert = utilAlerts.mostrarAlerts("Conductor ocupado",
                        "El conductor ya tiene otro viaje que se cruza con este horario ("
                                + temp.getFechaSalida().format(FMT) + " - "
                                + temp.getFechaLlegada().format(FMT) + ")","WARNING");
                alert.showAndWait();
                return;
            }
            if (viajeDao.validateVehiculo(temp)){
                Alert alert = utilAlerts.mostrarAlerts("Vehiculo ocupado",
                        "El vehiculo ya tiene otro viaje que se cruza con este horario ("
                                + temp.getFechaSalida().format(FMT) + " - "
                                + temp.getFechaLlegada().format(FMT) + ")","WARNING");
                alert.showAndWait();
                return;
            }
            viaje = temp;
            saved =true;
            cerrar();
        }
    }
    //metodo para enviar los datos al formulario y editar
    public void setViaje(Viaje v){
        this.viaje = v;
        if (v!=null){
            lblTitulo.setText("Editar Viaje");
            // seleccionamos por id porque son objetos distintos a los del combo
            for (Tarifa t : cbTarifa.getItems()){
                if (t.getIdtarifa() == v.getTarifa().getIdtarifa()){
                    cbTarifa.setValue(t);
                }
            }
            dpFecha.setValue(v.getFechaSalida().toLocalDate());
            for (Conductor c : cbConductor.getItems()){
                if (c.getIdconductor() == v.getConductor().getIdconductor()){
                    cbConductor.setValue(c);
                }
            }
            for (Vehiculo ve : cbVehiculo.getItems()){
                if (ve.getIdvehiculo() == v.getVehiculo().getIdvehiculo()){
                    cbVehiculo.setValue(ve);
                }
            }
        }
    }

}
