package entregable_u3.implementacion;

import entregable_u3.interfaces.ITarifaDao;
import entregable_u3.modelo.Tarifa;
import entregable_u3.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TarifaDaoImpl implements ITarifaDao {

    private Tarifa mapear(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();
        t.setIdtarifa(rs.getInt("idtarifa"));
        t.setOrigen(rs.getString("origen"));
        t.setDestino(rs.getString("destino"));
        t.setHoraSalida(LocalTime.parse(rs.getString("hora_salida")));
        t.setDuracionMin(rs.getInt("duracion_min"));
        t.setPrecio(rs.getDouble("precio"));
        return t;
    }

    @Override
    public boolean insert(Tarifa t) {
        boolean flag = false;
        String query = "INSERT INTO tarifa(origen,destino,hora_salida,duracion_min,precio)" +
                "VALUES(?,?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,t.getOrigen());
            st.setString(2,t.getDestino());
            st.setString(3,t.getHoraSalida().toString());
            st.setInt(4,t.getDuracionMin());
            st.setDouble(5,t.getPrecio());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al insertar tarifa:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Tarifa t) {
        boolean flag = false;
        String query = " UPDATE tarifa SET origen=?, destino=?, hora_salida=?, duracion_min=?, precio=? " +
                " WHERE idtarifa=? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,t.getOrigen());
            st.setString(2,t.getDestino());
            st.setString(3,t.getHoraSalida().toString());
            st.setInt(4,t.getDuracionMin());
            st.setDouble(5,t.getPrecio());
            st.setInt(6,t.getIdtarifa());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tarifa:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Tarifa searchById(int id) {
        Tarifa t = null;
        String query = "SELECT * FROM tarifa WHERE idtarifa=?;";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    t = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar tarifa por Id:" + e.getMessage());
        }
        return t;
    }

    @Override
    public boolean delete(Tarifa t) {
        boolean flag = false;
        String query = "DELETE FROM tarifa WHERE idtarifa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,t.getIdtarifa());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            // falla si la tarifa ya tiene viajes registrados
            System.out.println("Error al eliminar tarifa:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Tarifa> listAll() {
        List<Tarifa> lista = new ArrayList<>();
        String query = "SELECT * FROM tarifa ORDER BY origen, destino, hora_salida;";
        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
        ) {
            while (rs.next()){
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar tarifa:" + e.getMessage());
        }
        return lista;
    }
}
