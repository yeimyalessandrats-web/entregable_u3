package entregable_u3.modelo;

import javafx.beans.property.*;

import java.time.LocalTime;

public class Tarifa {
    private final IntegerProperty idtarifa = new SimpleIntegerProperty();
    private final StringProperty origen = new SimpleStringProperty();
    private final StringProperty destino = new SimpleStringProperty();
    private final ObjectProperty<LocalTime> horaSalida = new SimpleObjectProperty<>();
    private final IntegerProperty duracionMin = new SimpleIntegerProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();

    public Tarifa() {
    }

    public Tarifa(int idtarifa, String origen, String destino, LocalTime horaSalida,
                  int duracionMin, double precio) {
        setIdtarifa(idtarifa);
        setOrigen(origen);
        setDestino(destino);
        setHoraSalida(horaSalida);
        setDuracionMin(duracionMin);
        setPrecio(precio);
    }

    public final int getIdtarifa() {
        return idtarifa.get();
    }

    public final void setIdtarifa(int value) {
        idtarifa.set(value);
    }

    public final IntegerProperty idtarifaProperty() {
        return idtarifa;
    }


    public final String getOrigen() {
        return origen.get();
    }

    public final void setOrigen(String value) {
        origen.set(value);
    }

    public final StringProperty origenProperty() {
        return origen;
    }


    public final String getDestino() {
        return destino.get();
    }

    public final void setDestino(String value) {
        destino.set(value);
    }

    public final StringProperty destinoProperty() {
        return destino;
    }


    public final LocalTime getHoraSalida() {
        return horaSalida.get();
    }

    public final void setHoraSalida(LocalTime value) {
        horaSalida.set(value);
    }

    public final ObjectProperty<LocalTime> horaSalidaProperty() {
        return horaSalida;
    }


    public final int getDuracionMin() {
        return duracionMin.get();
    }

    public final void setDuracionMin(int value) {
        duracionMin.set(value);
    }

    public final IntegerProperty duracionMinProperty() {
        return duracionMin;
    }


    public final double getPrecio() {
        return precio.get();
    }

    public final void setPrecio(double value) {
        precio.set(value);
    }

    public final DoubleProperty precioProperty() {
        return precio;
    }

    // Texto que se muestra en los ComboBox
    @Override
    public String toString() {
        return getOrigen() + " -> " + getDestino() + " | " + getHoraSalida()
                + " | S/ " + String.format("%.2f", getPrecio());
    }
}
