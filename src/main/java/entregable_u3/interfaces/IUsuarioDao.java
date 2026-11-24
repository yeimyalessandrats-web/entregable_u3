package entregable_u3.interfaces;

import entregable_u3.modelo.Usuario;

public interface IUsuarioDao {

    public Usuario login(String user, String password);
    public boolean insert_user(Usuario u);

}
