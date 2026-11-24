package entregable_u2.test;

import entregable_u2.utils.ConexionDB;

import java.sql.Connection;

public class TestConexion {


    public static void testConexion() {
        try {
            Connection conn = ConexionDB.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexion success!!!");
            } else {
                System.out.println("Error conexion");
            }

        } catch (Exception e) {
            System.out.println("Error de conexion" + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        testConexion();
    }


}