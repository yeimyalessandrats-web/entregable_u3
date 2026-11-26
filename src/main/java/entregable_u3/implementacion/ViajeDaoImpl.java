package entregable_u3.implementacion;

import entregable_u3.interfaces.IViajeDao;
import entregable_u3.modelo.Conductor;
import entregable_u3.modelo.Tarifa;
import entregable_u3.modelo.Vehiculo;
import entregable_u3.modelo.Viaje;
import entregable_u3.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViajeDaoImpl implements IViajeDao {

    // fecha y hora se guardan como texto: 2026-11-01 08:30
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // El viaje llama a la tarifa, al conductor y al vehiculo
    private static final String SELECT_BASE =
            " SELECT vi.*, t.origen, t.destino, t.hora_salida, t.duracion_min, t.precio, " +
            " c.nombres AS c_nombres, c.apellidos AS c_apellidos, c.dni AS c_dni, " +
            " c.categoriaLicencia, ve.descripcion, ve.marca, ve.capacidad " +
            " FROM viaje vi, tarifa t, conductor c, vehiculo ve " +
            " WHERE vi.tarifaId = t.idtarifa " +
            " AND vi.conductorId = c.idconductor " +
            " AND vi.vehiculoId = ve.idvehiculo ";

    private Viaje mapear(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();
        t.setIdtarifa(rs.getInt("tarifaId"));
        t.setOrigen(rs.getString("origen"));
        t.setDestino(rs.getString("destino"));
        t.setHoraSalida(LocalTime.parse(rs.getString("hora_salida")));
        t.setDuracionMin(rs.getInt("duracion_min"));
        t.setPrecio(rs.getDouble("precio"));

        Conductor c = new Conductor();
        c.setIdconductor(rs.getInt("conductorId"));
        c.setNombres(rs.getString("c_nombres"));
        c.setApellidos(rs.getString("c_apellidos"));
        c.setDni(rs.getString("c_dni"));
        c.setCategoriaLicencia(rs.getString("categoriaLicencia"));

        Vehiculo ve = new Vehiculo();
        ve.setIdvehiculo(rs.getInt("vehiculoId"));
        ve.setDescripcion(rs.getString("descripcion"));
        ve.setMarca(rs.getString("marca"));
        ve.setCapacidad(rs.getInt("capacidad"));

        Viaje v = new Viaje();
        v.setIdviaje(rs.getInt("idviaje"));
        v.setFechaSalida(LocalDateTime.parse(rs.getString("fecha_salida"), FMT));
        v.setFechaLlegada(LocalDateTime.parse(rs.getString("fecha_llegada"), FMT));
        // estado y fecha y hora reales (pueden ser null si el viaje aun no sale o no llega)
        v.setEstado(rs.getString("estado") == null ? "E" : rs.getString("estado"));
        String salidaReal = rs.getString("fecha_salida_real");
        String llegadaReal = rs.getString("fecha_llegada_real");
        v.setFechaSalidaReal(salidaReal == null ? null : LocalDateTime.parse(salidaReal, FMT));
        v.setFechaLlegadaReal(llegadaReal == null ? null : LocalDateTime.parse(llegadaReal, FMT));
        v.setTarifa(t);
        v.setConductor(c);
        v.setVehiculo(ve);
        return v;
    }

    @Override
    public boolean insert(Viaje v) {
        boolean flag = false;
        String query = " INSERT INTO viaje(tarifaId,conductorId,vehiculoId,fecha_salida,fecha_llegada) " +
                " VALUES(?,?,?,?,?); ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,v.getTarifa().getIdtarifa());
            st.setInt(2,v.getConductor().getIdconductor());
            st.setInt(3,v.getVehiculo().getIdvehiculo());
            st.setString(4,v.getFechaSalida().format(FMT));
            st.setString(5,v.getFechaLlegada().format(FMT));
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al insertar viaje:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Viaje v) {
        boolean flag = false;
        String query = " UPDATE viaje SET tarifaId=?,conductorId=?,vehiculoId=?, " +
                " fecha_salida=?,fecha_llegada=? WHERE idviaje=? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,v.getTarifa().getIdtarifa());
            st.setInt(2,v.getConductor().getIdconductor());
            st.setInt(3,v.getVehiculo().getIdvehiculo());
            st.setString(4,v.getFechaSalida().format(FMT));
            st.setString(5,v.getFechaLlegada().format(FMT));
            st.setInt(6,v.getIdviaje());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar viaje:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Viaje searchById(int id) {
        Viaje v = null;
        String query = SELECT_BASE + " AND vi.idviaje = ? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    v = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar viaje por Id:" + e.getMessage());
        }
        return v;
    }

    @Override
    public boolean delete(Viaje v) {
        boolean flag = false;
        String query = "DELETE FROM viaje WHERE idviaje=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,v.getIdviaje());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            // falla si el viaje ya tiene pasajes vendidos
            System.out.println("Error al eliminar viaje:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Viaje> listAll() {
        List<Viaje> lista = new ArrayList<>();
        String query = SELECT_BASE + " ORDER BY vi.fecha_salida ";
        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
        ) {
            while (rs.next()){
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar viaje:" + e.getMessage());
        }
        return lista;
    }

    // Igual que updateStatus de ReservaDaoImpl: cambia el estado del viaje.
    // Ademas guarda la fecha y hora real de salida y de llegada (null si todavia no se registran)
    @Override
    public boolean updateStatus(Viaje v) {
        boolean flag = false;
        String query = " UPDATE viaje SET estado=?, fecha_salida_real=?, fecha_llegada_real=? WHERE idviaje=? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,v.getEstado());
            st.setString(2,v.getFechaSalidaReal() == null ? null : v.getFechaSalidaReal().format(FMT));
            st.setString(3,v.getFechaLlegadaReal() == null ? null : v.getFechaLlegadaReal().format(FMT));
            st.setInt(4,v.getIdviaje());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado del viaje:" + e.getMessage());
        }
        return flag;
    }

    // Igual que validateHabitacion del proyecto guia: dos viajes se cruzan cuando
    // la nueva salida es antes de la llegada del otro Y la nueva llegada es despues de su salida.
    // Se compara fecha Y hora. El idviaje <> ? evita compararse con el mismo viaje al editar.
    @Override
    public boolean validateConductor(Viaje v) {
        return existeCruce("conductorId", v.getConductor().getIdconductor(), v);
    }

    @Override
    public boolean validateVehiculo(Viaje v) {
        return existeCruce("vehiculoId", v.getVehiculo().getIdvehiculo(), v);
    }

    private boolean existeCruce(String columna, int id, Viaje v) {
        boolean ocupado = false;
        String query = " SELECT count(*) FROM viaje WHERE " + columna + "=? " +
                " AND idviaje <> ? " +
                " AND ( ? < fecha_llegada AND ? > fecha_salida ) ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,id);
            st.setInt(2,v.getIdviaje());
            st.setString(3,v.getFechaSalida().format(FMT));
            st.setString(4,v.getFechaLlegada().format(FMT));
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    ocupado = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar el cruce de horario:" + e.getMessage());
        }
        return ocupado;
    }
}
