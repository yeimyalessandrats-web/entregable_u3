package entregable_u3.controller;

import entregable_u3.HelloApplication;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Formulario de compra: el comprador (mayor de 18 años) elige el viaje y la cantidad de pasajes.
// El comprador viaja en el primer pasaje; si compra mas de uno, se indica quienes son los otros pasajeros.
public class PasajeFormController {
    private static UtilAlerts utilAlerts = new UtilAlerts();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private TextField txtDniComprador;
    @FXML
    private Label lblNombreComprador, lblDetalleViaje, lblDisponibles, lblTotal;
    // el pasaje llama al comprador (el que paga), al pasajero (el que viaja) y al viaje
    @FXML
    private ComboBox<Viaje> cbViaje;
    @FXML
    private Spinner<Integer> spCantidad;

    private final IPasajeroDao pasajeroDao = new PasajeroDaoImpl();
    private final IPasajeDao pasajeDao = new PasajeDaoImpl();

    private Pasajero compradorEncontrado;
    // los pasajes de la compra (los lee PasajeController para guardarlos con insertAll)
    private List<Pasaje> compra = new ArrayList<>();
    private boolean saved = false;

    @FXML
    public void initialize(){
        // solo se muestran los viajes que estan En espera (los que ya salieron no venden pasajes)
        for (Viaje v : new ViajeDaoImpl().listAll()){
            if ("E".equals(v.getEstado())){
                cbViaje.getItems().add(v);
            }
        }
        spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        // al elegir el viaje se muestra su horario, su precio y los asientos libres
        cbViaje.valueProperty().addListener((ov,oldValue,newValue)->{
            if (newValue != null){
                lblDetalleViaje.setText("Sale " + newValue.getFechaSalida().format(FMT)
                        + " - llega " + newValue.getFechaLlegada().format(FMT)
                        + " - S/ " + String.format("%.2f", newValue.getTarifa().getPrecio()));
                lblDisponibles.setText("Asientos libres: " + pasajeDao.availableSeats(newValue.getIdviaje()));
            }
            actualizarTotal();
        });
        // al cambiar la cantidad se actualiza el total
        spCantidad.valueProperty().addListener((ov,oldValue,newValue)->{
            actualizarTotal();
        });
        actualizarTotal();
    }

    // busca al comprador por dni: debe existir y ser mayor de 18 años
    @FXML
    private void buscarComprador(){
        String dni = txtDniComprador.getText();
        compradorEncontrado = null;
        if (dni.length()==8){
            Pasajero encontrado = pasajeroDao.searchByDni(dni);
            if (encontrado == null){
                lblNombreComprador.setText("No encontrado");
                lblNombreComprador.setStyle("-fx-text-fill: red;");
            }else if (!encontrado.esMayorDeEdad()){
                lblNombreComprador.setText("Es menor de edad, no puede comprar");
                lblNombreComprador.setStyle("-fx-text-fill: red;");
            }else{
                compradorEncontrado = encontrado;
                lblNombreComprador.setText(encontrado.getNombres()+" "+encontrado.getApellidos());
                lblNombreComprador.setStyle("-fx-text-fill: green;");
            }
        }else{
            lblNombreComprador.setText("Comprador no encontrado");
            lblNombreComprador.setStyle("-fx-text-fill: #8493A3;");
        }
    }

    // total = precio del viaje x cantidad de pasajes
    private void actualizarTotal(){
        Viaje v = cbViaje.getValue();
        if (v == null){
            lblTotal.setText("S/ 0.00");
        }else{
            lblTotal.setText(String.format("S/ %.2f", v.getTarifa().getPrecio() * spCantidad.getValue()));
        }
    }

    public boolean isSaved(){
        return saved;
    }
    // los pasajes de la compra: uno por cada pasajero
    public List<Pasaje> getCompra(){
        return compra;
    }
    private void advertir(String titulo, String mensaje){
        Alert alert = utilAlerts.mostrarAlerts(titulo, mensaje, "WARNING");
        alert.showAndWait();
    }
    private boolean validate(){
        if (compradorEncontrado == null || cbViaje.getValue() == null){
            Alert alert = utilAlerts.mostrarAlerts("Error",
                    "Busque al comprador (mayor de 18 años) y seleccione el viaje","ERROR");
            alert.showAndWait();
            return false;
        }
        if (UserSession.getInstance().getUserLogin() == null){
            Alert alert = utilAlerts.mostrarAlerts("ERROR",
                    "No hay un usuario en sesion","ERROR");
            alert.show();
            return false;
        }
        return true;
    }
    private void cerrar(){
        ((Stage) txtDniComprador.getScene().getWindow()).close();
    }

    // abre el formulario donde se indican los otros pasajeros (null si se cancela)
    private List<Pasajero> pedirAcompanantes(int cantidad, Viaje v){
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/entregable_u3/views/acompanante-form.fxml"));
            Parent root = loader.load();
            AcompananteFormController controller = loader.getController();
            controller.setDatos(cantidad, v.toString(), compradorEncontrado);
            Stage stage = new Stage();
            stage.setTitle("Pasajeros de la compra");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            return controller.isSaved() ? controller.getAcompanantes() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //actions of the views
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSave(){
        if (!validate()){
            return;
        }
        Viaje v = cbViaje.getValue();
        int cantidad = spCantidad.getValue();

        // no se pueden comprar mas pasajes que los asientos libres del viaje
        int libres = pasajeDao.availableSeats(v.getIdviaje());
        if (cantidad > libres){
            advertir("Capacidad completa", "El viaje solo tiene " + libres
                    + " asiento(s) libre(s) (capacidad del vehiculo)");
            return;
        }

        // pasajeros: el comprador viaja siempre; los demas se indican en otro formulario
        List<Pasajero> pasajeros = new ArrayList<>();
        pasajeros.add(compradorEncontrado);
        if (cantidad > 1){
            List<Pasajero> acompanantes = pedirAcompanantes(cantidad - 1, v);
            if (acompanantes == null){
                return;
            }
            pasajeros.addAll(acompanantes);
        }

        // fecha y hora en que se emite la compra (sin segundos), igual para todos los pasajes
        LocalDateTime emision = LocalDateTime.now().withSecond(0).withNano(0);
        List<Pasaje> temp = new ArrayList<>();
        for (Pasajero pa : pasajeros){
            Pasaje p = new Pasaje();
            p.setPasajero(pa);
            p.setComprador(compradorEncontrado);
            p.setViaje(v);
            p.setCosto(v.getTarifa().getPrecio());
            p.setFechaEmision(emision);
            p.setUsuario(UserSession.getInstance().getUserLogin());
            temp.add(p);
        }

        // validar a cada pasajero: un solo asiento por viaje y sin cruce de horarios
        for (Pasaje p : temp){
            if (pasajeDao.validateAsiento(p)){
                advertir("Pasaje repetido", p.getPasajero().getNombres() + " "
                        + p.getPasajero().getApellidos() + " ya tiene un pasaje para ese viaje");
                return;
            }
            Viaje cruce = pasajeDao.searchCruce(p);
            if (cruce != null){
                advertir("Horario cruzado", p.getPasajero().getNombres() + " "
                        + p.getPasajero().getApellidos() + " ya tiene el viaje "
                        + cruce.getTarifa().getOrigen() + " -> " + cruce.getTarifa().getDestino()
                        + " (" + cruce.getFechaSalida().format(FMT) + " - "
                        + cruce.getFechaLlegada().format(FMT) + ") que se cruza con este horario");
                return;
            }
        }
        // validar la capacidad de personas del vehiculo del viaje
        if (pasajeDao.validatedCapacity(temp)){
            advertir("Capacidad completa",
                    "El viaje ya no tiene asientos suficientes (capacidad del vehiculo)");
            return;
        }
        compra = temp;
        saved = true;
        cerrar();
    }

}
