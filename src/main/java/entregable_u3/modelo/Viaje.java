package entregable_u3.modelo;

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Viaje {
    private final IntegerProperty idviaje = new SimpleIntegerProperty();
    //fecha y hora (LocalDateTime) de salida y de llegada
    private final ObjectProperty<LocalDateTime> fechaSalida = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> fechaLlegada = new SimpleObjectProperty<>();
    //asociacion de las llaves foraneas: el viaje llama a la tarifa, al conductor y al vehiculo
    private final ObjectProperty<Tarifa> tarifa = new SimpleObjectProperty<>();
    private final ObjectProperty<Conductor> conductor = new SimpleObjectProperty<>();
    private final ObjectProperty<Vehiculo> vehiculo = new SimpleObjectProperty<>();

    public Viaje() {
    }

    public Viaje(int idviaje, LocalDateTime fechaSalida, LocalDateTime fechaLlegada,
                 Tarifa tarifa, Conductor conductor, Vehiculo vehiculo) {
        setIdviaje(idviaje);
        setFechaSalida(fechaSalida);
        setFechaLlegada(fechaLlegada);
        setTarifa(tarifa);
        setConductor(conductor);
        setVehiculo(vehiculo);
    }

    public final int getIdviaje() {
        return idviaje.get();
    }

    public final void setIdviaje(int value) {
        idviaje.set(value);
    }

    public final IntegerProperty idviajeProperty() {
        return idviaje;
    }


    public final LocalDateTime getFechaSalida() {
        return fechaSalida.get();
    }

    public final void setFechaSalida(LocalDateTime value) {
        fechaSalida.set(value);
    }

    public final ObjectProperty<LocalDateTime> fechaSalidaProperty() {
        return fechaSalida;
    }


    public final LocalDateTime getFechaLlegada() {
        return fechaLlegada.get();
    }

    public final void setFechaLlegada(LocalDateTime value) {
        fechaLlegada.set(value);
    }

    public final ObjectProperty<LocalDateTime> fechaLlegadaProperty() {
        return fechaLlegada;
    }


    public final Tarifa getTarifa() {
        return tarifa.get();
    }

    public final void setTarifa(Tarifa value) {
        tarifa.set(value);
    }

    public final ObjectProperty<Tarifa> tarifaProperty() {
        return tarifa;
    }


    public final Conductor getConductor() {
        return conductor.get();
    }

    public final void setConductor(Conductor value) {
        conductor.set(value);
    }

    public final ObjectProperty<Conductor> conductorProperty() {
        return conductor;
    }


    public final Vehiculo getVehiculo() {
        return vehiculo.get();
    }

    public final void setVehiculo(Vehiculo value) {
        vehiculo.set(value);
    }

    public final ObjectProperty<Vehiculo> vehiculoProperty() {
        return vehiculo;
    }

    // Texto que se muestra en los ComboBox
    @Override
    public String toString() {
        return "#" + getIdviaje() + " " + getTarifa().getOrigen() + " -> " + getTarifa().getDestino()
                + " (" + getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ")";
    }
}
