package entregable_u3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import entregable_u3.implementacion.PasajeDaoImpl;
import entregable_u3.implementacion.PasajeroDaoImpl;
import entregable_u3.implementacion.ViajeDaoImpl;
import entregable_u3.interfaces.IPasajeDao;
import entregable_u3.interfaces.IPasajeroDao;
import entregable_u3.modelo.Pasaje;
import entregable_u3.modelo.Pasajero;
import entregable_u3.modelo.UserSession;
import entregable_u3.modelo.Viaje;
import entregable_u3.utils.UtilAlerts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PasajeFormController {
    private static UtilAlerts utilAlerts = new UtilAlerts();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtDniBusqueda, txtCosto;
    @FXML
    private Label lblNombrePasajero, lblDetalleViaje;
    // el pasaje llama al pasajero (se busca por dni) y al viaje
    @FXML
    private ComboBox<Viaje> cbViaje;

    private final IPasajeroDao pasajeroDao = new PasajeroDaoImpl();
    private final IPasajeDao pasajeDao = new PasajeDaoImpl();

    private Pasajero pasajeroEncontrado;
    private Pasaje pasaje;
    private boolean saved = false;

    @FXML
    public void initialize(){
        // solo se muestran los viajes que todavia no han salido (fecha y hora)
        for (Viaje v : new ViajeDaoImpl().listAll()){
            if (v.getFechaSalida().isAfter(LocalDateTime.now())){
                cbViaje.getItems().add(v);
            }
        }
        // al elegir el viaje se carga el precio de su tarifa
        cbViaje.valueProperty().addListener((ov,oldValue,newValue)->{
            if (newValue != null){
                lblDetalleViaje.setText("Sale " + newValue.getFechaSalida().format(FMT)
                        + " - llega " + newValue.getFechaLlegada().format(FMT));
                txtCosto.setText(String.format(Locale.US,"%.2f", newValue.getTarifa().getPrecio()));
            }
        });
    }

    @FXML
    private void buscarPasajero(){
        String dni = txtDniBusqueda.getText();
        if (dni.length()==8){
            pasajeroEncontrado = pasajeroDao.searchByDni(dni);
            if (pasajeroEncontrado !=null){
                lblNombrePasajero.setText(pasajeroEncontrado.getNombres()+" "
                        + pasajeroEncontrado.getApellidos());
                lblNombrePasajero.setStyle("-fx-text-fill: green;");
            }else{
                lblNombrePasajero.setText("No encontrado");
                lblNombrePasajero.setStyle("-fx-text-fill: red;");
            }
        }else{
            pasajeroEncontrado = null;
            lblNombrePasajero.setText("Pasajero no encontrado");
            lblNombrePasajero.setStyle("-fx-text-fill: #8493A3;");
        }
    }

    public boolean isSaved(){
        return saved;
    }
    public Pasaje getPasaje(){
        return pasaje;
    }
    private boolean validate(){
        if (pasajeroEncontrado == null || cbViaje.getValue() == null){
            Alert alert = utilAlerts.mostrarAlerts("Error","Complete los campos",
                    "ERROR");
            alert.showAndWait();
            return false;
        }
        try {
            if (Double.parseDouble(txtCosto.getText().trim().replace(',','.')) <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "El costo debe ser un numero mayor a 0","WARNING");
            alert.show();
            return false;
        }
        if (pasaje == null && UserSession.getInstance().getUserLogin() == null){
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "No hay un usuario en sesion","ERROR");
            alert.show();
            return false;
        }
        return true;
    }
    private void cerrar(){
        ((Stage) txtDniBusqueda.getScene().getWindow()).close();
    }
    //actions of the views
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSave(){
        if (validate()){
            Pasaje temp = new Pasaje();
            temp.setIdpasaje(pasaje == null ? 0 : pasaje.getIdpasaje());
            temp.setCodigo(pasaje == null ? null : pasaje.getCodigo());
            temp.setPasajero(pasajeroEncontrado);
            temp.setViaje(cbViaje.getValue());
            temp.setCosto(Double.parseDouble(txtCosto.getText().trim().replace(',','.')));
            // fecha y hora en que se emite el pasaje (sin segundos)
            temp.setFechaEmision(pasaje == null
                    ? LocalDateTime.now().withSecond(0).withNano(0) : pasaje.getFechaEmision());
            temp.setUsuario(pasaje == null
                    ? UserSession.getInstance().getUserLogin() : pasaje.getUsuario());

            // validar la capacidad de personas del vehiculo del viaje
            if (pasajeDao.validatedCapacity(temp)){
                Alert alert = utilAlerts.mostrarAlerts("Capacidad completa",
                        "El viaje ya no tiene asientos disponibles (capacidad del vehiculo)","WARNING");
                alert.showAndWait();
                return;
            }
            pasaje = temp;
            saved = true;
            cerrar();
        }
    }
    //metodo para enviar los datos al formulario y editar
    public void setPasaje(Pasaje p){
        this.pasaje = p;
        if (p!=null){
            lblTitulo.setText("Editar Pasaje " + p.getCodigo());
            txtDniBusqueda.setText(p.getPasajero().getDni());
            buscarPasajero();
            // el viaje del pasaje se muestra aunque ya haya salido
            Viaje actual = null;
            for (Viaje v : cbViaje.getItems()){
                if (v.getIdviaje() == p.getViaje().getIdviaje()){
                    actual = v;
                }
            }
            if (actual == null){
                actual = new ViajeDaoImpl().searchById(p.getViaje().getIdviaje());
                cbViaje.getItems().add(actual);
            }
            cbViaje.setValue(actual);
            txtCosto.setText(String.format(Locale.US,"%.2f", p.getCosto()));
        }
    }

}
