package entregable_u2.interfaces;

import entregable_u2.modelo.Usuario;

public interface IUsuarioDao {

    public Usuario login(String user, String password);
    public boolean insert_user(Usuario u);

}
