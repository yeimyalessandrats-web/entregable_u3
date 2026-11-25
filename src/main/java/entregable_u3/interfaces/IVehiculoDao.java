package entregable_u3.interfaces;

import entregable_u3.modelo.Vehiculo;

import java.util.List;

public interface IVehiculoDao {
    public boolean insert(Vehiculo v);

    public boolean update(Vehiculo v);

    public boolean delete(Vehiculo v);

    public Vehiculo searchById(int id);

    public List<Vehiculo> listAll();
}