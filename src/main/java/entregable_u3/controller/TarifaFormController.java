package entregable_u3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import entregable_u3.modelo.Tarifa;
import entregable_u3.utils.UtilAlerts;

import java.time.LocalTime;

public class TarifaFormController {
    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtOrigen,txtDestino,txtHora,txtDuracion,txtPrecio;
    private Tarifa tarifa;
    private boolean saved = false;

    public boolean isSaved(){
        return saved;
    }
    public Tarifa getTarifa(){
        return tarifa;
    }
    private boolean validate(){
        if (txtOrigen.getText().isBlank() || txtDestino.getText().isBlank()){
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "Origen y destino son obligatorios","WARNING");
            alert.show();
            return false;
        }
        if (txtOrigen.getText().trim().equalsIgnoreCase(txtDestino.getText().trim())){
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "El origen y el destino no pueden ser iguales","WARNING");
            alert.show();
            return false;
        }
        try {
            LocalTime.parse(txtHora.getText().trim());
        } catch (Exception e) {
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "La hora debe tener el formato HH:mm, ejm: 06:30","WARNING");
            alert.show();
            return false;
        }
        try {
            if (Integer.parseInt(txtDuracion.getText().trim()) <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "La duracion (en minutos) debe ser un numero mayor a 0","WARNING");
            alert.show();
            return false;
        }
        try {
            if (Double.parseDouble(txtPrecio.getText().trim().replace(',','.')) <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "El precio debe ser un numero mayor a 0","WARNING");
            alert.show();
            return false;
        }
        return true;
    }
    private void cerrar(){
        Stage stage = (Stage) txtOrigen.getScene().getWindow();
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
            if (tarifa == null)
                tarifa = new Tarifa();
            tarifa.setOrigen(txtOrigen.getText().trim());
            tarifa.setDestino(txtDestino.getText().trim());
            tarifa.setHoraSalida(LocalTime.parse(txtHora.getText().trim()));
            tarifa.setDuracionMin(Integer.parseInt(txtDuracion.getText().trim()));
            tarifa.setPrecio(Double.parseDouble(txtPrecio.getText().trim().replace(',','.')));
            saved =true;
            cerrar();
        }
    }
    //metodo para enviar los datos al formulario y editar
    public void setTarifa(Tarifa t){
        this.tarifa = t;
        if (t!=null){
            lblTitulo.setText("Editar Tarifa");
            txtOrigen.setText(t.getOrigen());
            txtDestino.setText(t.getDestino());
            txtHora.setText(t.getHoraSalida().toString());
            txtDuracion.setText(String.valueOf(t.getDuracionMin()));
            txtPrecio.setText(String.valueOf(t.getPrecio()));
        }
    }

}
