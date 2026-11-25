package entregable_u3.interfaces;

import entregable_u3.modelo.Pasaje;

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
}
