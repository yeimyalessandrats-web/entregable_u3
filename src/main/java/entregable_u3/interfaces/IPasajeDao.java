package entregable_u3.interfaces;

import entregable_u3.modelo.Pasaje;
import entregable_u3.modelo.Viaje;

import java.util.List;

public interface IPasajeDao {
    public boolean insert(Pasaje p);
    public boolean update(Pasaje p);
    public Pasaje searchById(int id);
    public boolean delete(Pasaje p);
    public List<Pasaje> listAll();
    // busca los pasajes por el dni del pasajero
    public List<Pasaje> searchByDni(String dni);
    // true si el viaje ya vendio todos los asientos del vehiculo (capacidad de personas)
    public boolean validatedCapacity(Pasaje p);
    // guarda todos los pasajes de una compra (cantidad) en una sola transaccion
    public boolean insertAll(List<Pasaje> lista);
    // true si algun viaje de la compra no tiene asientos suficientes (capacidad del vehiculo)
    public boolean validatedCapacity(List<Pasaje> lista);
    // asientos que todavia estan libres en un viaje
    public int availableSeats(int idviaje);
    // true si el pasajero ya tiene un pasaje en otro viaje cuyo horario se cruza con el del pasaje
    public boolean validateHorario(Pasaje p);
    // true si el pasajero ya tiene un pasaje en ese mismo viaje (una persona ocupa un solo asiento por viaje)
    public boolean validateAsiento(Pasaje p);
    // devuelve el viaje del pasajero que se cruza con el del pasaje (null si no hay cruce)
    public Viaje searchCruce(Pasaje p);
}
