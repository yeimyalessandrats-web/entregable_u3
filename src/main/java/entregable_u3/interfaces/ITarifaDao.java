package entregable_u3.interfaces;

import entregable_u3.modelo.Tarifa;

import java.util.List;

public interface ITarifaDao {
    public boolean insert(Tarifa t);

    public boolean update(Tarifa t);

    public Tarifa searchById(int id);

    public boolean delete(Tarifa t);

    public List<Tarifa> listAll();
}
