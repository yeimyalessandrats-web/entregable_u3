package entregable_u3.modelo;

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Viaje {
    private final IntegerProperty idviaje = new SimpleIntegerProperty();
    //fecha y hora (LocalDateTime) de salida y de llegada
    private final ObjectProperty<LocalDateTime> fechaSalida = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> fechaLlegada = new SimpleObjectProperty<>();
    //fecha y hora REALES: el viaje muchas veces no sale ni llega a la hora establecida (null hasta que se registren)
    private final ObjectProperty<LocalDateTime> fechaSalidaReal = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> fechaLlegadaReal = new SimpleObjectProperty<>();
    //estado del viaje: E = En espera, T = En transito, D = En destino
    private final StringProperty estado = new SimpleStringProperty("E");
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


    public final LocalDateTime getFechaSalidaReal() {
        return fechaSalidaReal.get();
    }

    public final void setFechaSalidaReal(LocalDateTime value) {
        fechaSalidaReal.set(value);
    }

    public final ObjectProperty<LocalDateTime> fechaSalidaRealProperty() {
        return fechaSalidaReal;
    }

    public final LocalDateTime getFechaLlegadaReal() {
        return fechaLlegadaReal.get();
    }

    public final void setFechaLlegadaReal(LocalDateTime value) {
        fechaLlegadaReal.set(value);
    }

    public final ObjectProperty<LocalDateTime> fechaLlegadaRealProperty() {
        return fechaLlegadaReal;
    }

    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value);
    }

    public final StringProperty estadoProperty() {
        return estado;
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

    // true si este viaje se cruza en fecha y hora con otro: una persona no puede estar en dos viajes a la vez.
    // Se permite salir justo a la hora en que llega el otro viaje.
    public boolean seCruzaCon(Viaje otro) {
        if (getIdviaje() == otro.getIdviaje()) {
            return false;
        }
        return getFechaSalida().isBefore(otro.getFechaLlegada())
                && getFechaLlegada().isAfter(otro.getFechaSalida());
    }

    // Ruta con su horario completo, ejm: Tarapoto -> Moyobamba (01/11/2026 10:00 - 01/11/2026 12:00)
    public String resumenHorario() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return getTarifa().getOrigen() + " -> " + getTarifa().getDestino()
                + " (" + getFechaSalida().format(f) + " - " + getFechaLlegada().format(f) + ")";
    }

    // Texto que se muestra en los ComboBox
    @Override
    public String toString() {
        return "#" + getIdviaje() + " " + getTarifa().getOrigen() + " -> " + getTarifa().getDestino()
                + " (" + getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ")";
    }
}
