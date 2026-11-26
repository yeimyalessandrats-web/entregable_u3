package entregable_u3.modelo;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Pasaje {
    private final IntegerProperty idpasaje = new SimpleIntegerProperty();
    //codigo que se genera automaticamente al registrar el pasaje, ejm: PAS-2026-00001
    private final StringProperty codigo = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> fechaEmision = new SimpleObjectProperty<>();
    private final DoubleProperty costo = new SimpleDoubleProperty();
    //asociacion de las llaves foraneas: el pasaje llama al pasajero (el que viaja), al comprador (el que paga) y al viaje
    private final ObjectProperty<Pasajero> pasajero = new SimpleObjectProperty<>();
    private final ObjectProperty<Pasajero> comprador = new SimpleObjectProperty<>();
    private final ObjectProperty<Viaje> viaje = new SimpleObjectProperty<>();
    private final ObjectProperty<Usuario> usuario = new SimpleObjectProperty<>();

    public Pasaje() {
    }

    public final int getIdpasaje() {
        return idpasaje.get();
    }

    public final void setIdpasaje(int value) {
        idpasaje.set(value);
    }

    public final IntegerProperty idpasajeProperty() {
        return idpasaje;
    }


    public final String getCodigo() {
        return codigo.get();
    }

    public final void setCodigo(String value) {
        codigo.set(value);
    }

    public final StringProperty codigoProperty() {
        return codigo;
    }


    public final LocalDateTime getFechaEmision() {
        return fechaEmision.get();
    }

    public final void setFechaEmision(LocalDateTime value) {
        fechaEmision.set(value);
    }

    public final ObjectProperty<LocalDateTime> fechaEmisionProperty() {
        return fechaEmision;
    }


    public final double getCosto() {
        return costo.get();
    }

    public final void setCosto(double value) {
        costo.set(value);
    }

    public final DoubleProperty costoProperty() {
        return costo;
    }


    public final Pasajero getPasajero() {
        return pasajero.get();
    }

    public final void setPasajero(Pasajero value) {
        pasajero.set(value);
    }

    public final ObjectProperty<Pasajero> pasajeroProperty() {
        return pasajero;
    }


    public final Pasajero getComprador() {
        return comprador.get();
    }

    public final void setComprador(Pasajero value) {
        comprador.set(value);
    }

    public final ObjectProperty<Pasajero> compradorProperty() {
        return comprador;
    }


    public final Viaje getViaje() {
        return viaje.get();
    }

    public final void setViaje(Viaje value) {
        viaje.set(value);
    }

    public final ObjectProperty<Viaje> viajeProperty() {
        return viaje;
    }


    public final Usuario getUsuario() {
        return usuario.get();
    }

    public final void setUsuario(Usuario value) {
        usuario.set(value);
    }

    public final ObjectProperty<Usuario> usuarioProperty() {
        return usuario;
    }

}
