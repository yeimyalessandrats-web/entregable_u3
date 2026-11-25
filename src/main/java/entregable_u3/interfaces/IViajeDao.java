package entregable_u3.interfaces;

import entregable_u3.modelo.Viaje;

import java.util.List;

public interface IViajeDao {
    public boolean insert(Viaje v);
    public boolean update(Viaje v);
    public Viaje searchById(int id);
    public boolean delete(Viaje v);
    public List<Viaje> listAll();
    // true si el conductor ya tiene otro viaje que se cruza en fecha y hora
    public boolean validateConductor(Viaje v);
    // true si el vehiculo ya tiene otro viaje que se cruza en fecha y hora
    public boolean validateVehiculo(Viaje v);
}
