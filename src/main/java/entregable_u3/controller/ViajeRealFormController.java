package entregable_u3.controller;

import entregable_u3.modelo.Viaje;
import entregable_u3.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Formulario para registrar la fecha y hora REAL de salida o de llegada de un viaje
public class ViajeRealFormController {
    private static UtilAlerts utilAlerts = new UtilAlerts();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("H:mm");

    @FXML
    private Label lblTitulo, lblViaje, lblProgramado, lblTextoFecha, lblTextoHora;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtHora;

    private Viaje viaje;
    private boolean esSalida = true;
    private LocalDateTime fechaHora;
    private boolean saved = false;

    @FXML
    public void initialize(){
        // por defecto se propone la fecha y hora actual
        dpFecha.setValue(LocalDate.now());
        txtHora.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public boolean isSaved(){
        return saved;
    }
    public LocalDateTime getFechaHora(){
        return fechaHora;
    }

    // convierte la fecha y la hora escritas en un LocalDateTime (null si la hora esta mal escrita)
    private LocalDateTime leerFechaHora(){
        try {
            LocalTime hora = LocalTime.parse(txtHora.getText().trim(), HORA);
            return LocalDateTime.of(dpFecha.getValue(), hora);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    private void advertir(String titulo, String mensaje){
        Alert alert = utilAlerts.mostrarAlerts(titulo, mensaje, "WARNING");
        alert.showAndWait();
    }
    private boolean validate(){
        if (dpFecha.getValue() == null || txtHora.getText().trim().isEmpty()){
            advertir("ERROR", "Ingrese la fecha y la hora");
            return false;
        }
        LocalDateTime temp = leerFechaHora();
        if (temp == null){
            advertir("ERROR", "La hora no es valida, use el formato HH:mm (ejemplo 06:45)");
            return false;
        }
        // se registra lo que ya ocurrio, no puede ser una fecha y hora futura
        if (temp.isAfter(LocalDateTime.now())){
            advertir("ERROR", "La fecha y hora real no puede ser futura");
            return false;
        }
        if (!esSalida && viaje.getFechaSalidaReal() != null && temp.isBefore(viaje.getFechaSalidaReal())){
            advertir("ERROR", "La llegada no puede ser antes de la salida real ("
                    + viaje.getFechaSalidaReal().format(FMT) + ")");
            return false;
        }
        if (esSalida && viaje.getFechaLlegadaReal() != null && temp.isAfter(viaje.getFechaLlegadaReal())){
            advertir("ERROR", "La salida no puede ser despues de la llegada real ("
                    + viaje.getFechaLlegadaReal().format(FMT) + ")");
            return false;
        }
        return true;
    }
    private void cerrar(){
        ((Stage) txtHora.getScene().getWindow()).close();
    }
    //actions of the views
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSave(){
        if (validate()){
            fechaHora = leerFechaHora();
            saved = true;
            cerrar();
        }
    }
    //metodo para enviar el viaje al formulario y saber si es la salida o la llegada
    public void setViaje(Viaje v, boolean esSalida){
        this.viaje = v;
        this.esSalida = esSalida;
        lblTitulo.setText(esSalida ? "Registrar salida real" : "Registrar llegada real");
        lblTextoFecha.setText(esSalida ? "Fecha de salida" : "Fecha de llegada");
        lblTextoHora.setText(esSalida ? "Hora de salida" : "Hora de llegada");
        lblViaje.setText(v.toString());
        LocalDateTime programado = esSalida ? v.getFechaSalida() : v.getFechaLlegada();
        lblProgramado.setText(programado.format(FMT));
        // si ya se habia registrado, se muestra para poder corregirlo
        LocalDateTime actual = esSalida ? v.getFechaSalidaReal() : v.getFechaLlegadaReal();
        if (actual != null){
            dpFecha.setValue(actual.toLocalDate());
            txtHora.setText(actual.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

}
