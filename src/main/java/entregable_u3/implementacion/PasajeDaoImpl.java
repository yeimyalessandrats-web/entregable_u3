package entregable_u3.implementacion;

import entregable_u3.interfaces.IPasajeDao;
import entregable_u3.modelo.Pasaje;
import entregable_u3.modelo.Pasajero;
import entregable_u3.modelo.Tarifa;
import entregable_u3.modelo.Usuario;
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

public class PasajeDaoImpl implements IPasajeDao {

    // fecha y hora se guardan como texto: 2026-11-01 08:30
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // El pasaje llama al pasajero y al viaje (el viaje trae su tarifa)
    private static final String SELECT_BASE =
            " SELECT p.*, pa.nombres AS pa_nombres, pa.apellidos AS pa_apellidos, pa.dni AS pa_dni, " +
            " vi.fecha_salida, vi.fecha_llegada, vi.tarifaId, " +
            " t.origen, t.destino, t.hora_salida, t.duracion_min, t.precio, u.user " +
            " FROM pasaje p, pasajero pa, viaje vi, tarifa t, usuario u " +
            " WHERE p.pasajeroId = pa.idpasajero " +
            " AND p.viajeId = vi.idviaje " +
            " AND vi.tarifaId = t.idtarifa " +
            " AND p.usuarioId = u.idusuario ";

    private Pasaje mapear(ResultSet rs) throws SQLException {
        Pasajero pa = new Pasajero();
        pa.setIdpasajero(rs.getInt("pasajeroId"));
        pa.setNombres(rs.getString("pa_nombres"));
        pa.setApellidos(rs.getString("pa_apellidos"));
        pa.setDni(rs.getString("pa_dni"));

        Tarifa t = new Tarifa();
        t.setIdtarifa(rs.getInt("tarifaId"));
        t.setOrigen(rs.getString("origen"));
        t.setDestino(rs.getString("destino"));
        t.setHoraSalida(LocalTime.parse(rs.getString("hora_salida")));
        t.setDuracionMin(rs.getInt("duracion_min"));
        t.setPrecio(rs.getDouble("precio"));

        Viaje vi = new Viaje();
        vi.setIdviaje(rs.getInt("viajeId"));
        vi.setFechaSalida(LocalDateTime.parse(rs.getString("fecha_salida"), FMT));
        vi.setFechaLlegada(LocalDateTime.parse(rs.getString("fecha_llegada"), FMT));
        vi.setTarifa(t);

        Usuario u = new Usuario();
        u.setIdusuario(rs.getInt("usuarioId"));
        u.setUser(rs.getString("user"));

        Pasaje p = new Pasaje();
        p.setIdpasaje(rs.getInt("idpasaje"));
        p.setCodigo(rs.getString("codigo"));
        p.setFechaEmision(LocalDateTime.parse(rs.getString("fecha_emision"), FMT));
        p.setCosto(rs.getDouble("costo"));
        p.setPasajero(pa);
        p.setViaje(vi);
        p.setUsuario(u);
        return p;
    }

    // Genera el codigo del pasaje: PAS-<anio>-<id con 5 digitos>, ejm: PAS-2026-00001
    // El id que se usara se calcula antes de insertar, asi codigo e id siempre coinciden
    private int siguienteId(Connection cn) throws SQLException {
        String query = " SELECT MAX(COALESCE((SELECT seq FROM sqlite_sequence WHERE name='pasaje'),0), " +
                " COALESCE((SELECT MAX(idpasaje) FROM pasaje),0)) + 1 ";
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }

    @Override
    public boolean insert(Pasaje p) {
        boolean flag = false;
        String query = " INSERT INTO pasaje(idpasaje,codigo,pasajeroId,viajeId,fecha_emision,costo,usuarioId) " +
                " VALUES(?,?,?,?,?,?,?) ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            int id = siguienteId(cn);
            String codigo = String.format("PAS-%d-%05d", p.getFechaEmision().getYear(), id);
            st.setInt(1,id);
            st.setString(2,codigo);
            st.setInt(3,p.getPasajero().getIdpasajero());
            st.setInt(4,p.getViaje().getIdviaje());
            st.setString(5,p.getFechaEmision().format(FMT));
            st.setDouble(6,p.getCosto());
            st.setInt(7,p.getUsuario().getIdusuario());
            st.executeUpdate();
            //devolvemos el codigo generado al objeto
            p.setIdpasaje(id);
            p.setCodigo(codigo);
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al insertar pasaje:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Pasaje p) {
        boolean flag = false;
        // el codigo no se modifica
        String query = " UPDATE pasaje SET pasajeroId=?, viajeId=?, costo=? WHERE idpasaje=? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,p.getPasajero().getIdpasajero());
            st.setInt(2,p.getViaje().getIdviaje());
            st.setDouble(3,p.getCosto());
            st.setInt(4,p.getIdpasaje());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar pasaje:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Pasaje searchById(int id) {
        Pasaje p = null;
        String query = SELECT_BASE + " AND p.idpasaje = ? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    p = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar pasaje por Id:" + e.getMessage());
        }
        return p;
    }

    @Override
    public boolean delete(Pasaje p) {
        boolean flag = false;
        String query = "DELETE FROM pasaje WHERE idpasaje=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,p.getIdpasaje());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar pasaje:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Pasaje> listAll() {
        List<Pasaje> lista = new ArrayList<>();
        String query = SELECT_BASE + " ORDER BY p.idpasaje DESC ";
        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
        ) {
            while (rs.next()){
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pasaje:" + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Pasaje> searchByDni(String dni) {
        List<Pasaje> lista = new ArrayList<>();
        String query = SELECT_BASE + " AND pa.dni LIKE ? ORDER BY p.idpasaje DESC ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,dni + "%");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()){
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar pasaje por dni:" + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean validatedCapacity(Pasaje p) {
        boolean lleno = false;
        // pasajes ya vendidos del viaje (sin contar el que se esta editando) vs capacidad del vehiculo
        String query = " SELECT (SELECT COUNT(*) FROM pasaje WHERE viajeId = ? AND idpasaje <> ?) AS vendidos, " +
                " ve.capacidad FROM viaje vi, vehiculo ve " +
                " WHERE vi.vehiculoId = ve.idvehiculo AND vi.idviaje = ? ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,p.getViaje().getIdviaje());
            st.setInt(2,p.getIdpasaje());
            st.setInt(3,p.getViaje().getIdviaje());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    lleno = rs.getInt("vendidos") >= rs.getInt("capacidad");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar la capacidad del viaje:" + e.getMessage());
        }
        return lleno;
    }
}
