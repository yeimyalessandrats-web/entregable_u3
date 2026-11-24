package entregable_u3.interfaces;

import entregable_u3.modelo.Pasajero;

import java.util.List;

public interface IPasajeroDao {

    public boolean insert(Pasajero p);
    public boolean update(Pasajero p);
    public boolean delete(Pasajero p);
    public Pasajero searchById(int id);
    public Pasajero searchByDni(String dni);
    public List<Pasajero> listAll();

}
