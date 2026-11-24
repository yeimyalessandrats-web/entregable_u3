package entregable_u2.implementacion;

import entregable_u2.interfaces.IUsuarioDao;
import entregable_u2.modelo.Usuario;
import entregable_u2.utils.ConexionDB;

import java.sql.*;

public class UsuarioDaoImpl implements IUsuarioDao {

    @Override
    public Usuario login(String user, String password) {
        Usuario us = null;
        String query = " SELECT * FROM usuario WHERE user = ? AND password = ?; ";
        try(Connection cn = ConexionDB.getConnection();
            PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,user);
            st.setString(2,password);
            try(ResultSet rs = st.executeQuery()){
                if (rs.next()){
                    us = new Usuario();
                    //Data table user
                    us.setIdusuario(rs.getInt("idusuario"));
                    us.setUser(rs.getString("user"));
                    us.setTipoUser(rs.getString("tipoUser"));
                    //Data heredada
                    us.setNombres(rs.getString("nombre"));
                    us.setApellidos(rs.getString("apellidos"));
                    us.setDni(rs.getString("dni"));
                    System.out.println("Login exitoso");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en el login"+e.getMessage());
        }
        return us;
    }

    @Override
    public boolean insert_user(Usuario u) {
        boolean flag = false;
        String query = " INSERT INTO usuario(user,password,tipoUser,nombre,apellidos,dni) VALUES(?,?,?,?,?,?); ";
        try(Connection cn = ConexionDB.getConnection();
            PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,u.getUser());
            st.setString(2,u.getPassword());
            st.setString(3,u.getTipoUser());
            st.setString(4,u.getNombres());
            st.setString(5,u.getApellidos());
            st.setString(6,u.getDni());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario"+e.getMessage());
        }
        return flag;
    }
}
