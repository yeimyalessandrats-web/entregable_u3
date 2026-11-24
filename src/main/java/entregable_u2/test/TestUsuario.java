package entregable_u2.test;

import entregable_u2.implementacion.UsuarioDaoImpl;
import entregable_u2.interfaces.IUsuarioDao;
import entregable_u2.modelo.Usuario;

public class TestUsuario {
    private static IUsuarioDao dao = new UsuarioDaoImpl();

  public void valid_login(){
      Usuario us = dao.login("admin","admin123");
      if (us !=null){
          System.out.println("ID_USUARIO: "+us.getIdusuario());
          System.out.println("NOMBRE: "+us.getNombres());
          System.out.println("APELLIDOS: "+us.getApellidos());
          System.out.println("DNI: "+us.getDni());
          System.out.println("USER: "+us.getUser());
          System.out.println("TIPO_USER:"+us.getTipoUser());
      }else{
          System.out.println("error de login");
      }
  }

    public static void main(String[] args) {
        TestUsuario t = new TestUsuario();
        t.valid_login();
    }

}
