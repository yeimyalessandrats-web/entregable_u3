package entregable_u3.utils;

import java.io.File;
import java.sql.*;

public class ConexionDB {
    private static Connection connection;
    private static boolean iniciada = false;

    //connection mode prod
    public static Connection getConnection(){
        try{
            if (connection == null || connection.isClosed()){
                //1. Obtener la carpeta del usuario actual
                String userHome = System.getProperty("user.home");
                File directory = new File(userHome+"/SistemaTransporte");
                //2. si la carpeta no existe, la creamos
                if (!directory.exists()){
                    directory.mkdirs();
                }
                //la bd se guardara C:\Users\Nombre\SistemaTransporte\transporte.db
                String dbPath =directory.getAbsolutePath()+"/transporte.db";
                String url = "jdbc:sqlite:"+dbPath;
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);
                try(Statement stmt = connection.createStatement()){
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
                //crear las tablas en la bd
                if (!iniciada){
                    initDatabase();
                    iniciada = true;
                }
                System.out.println("Conexion success");
            }
            return connection;

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a la BD" +e.getMessage());
        }

    }
    private static void initDatabase() {
        // Usamos CREATE TABLE IF NOT EXISTS para que no de error si ya existen
        String sqlConductor = "CREATE TABLE IF NOT EXISTS conductor (" +
                "idconductor INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombres TEXT NOT NULL, apellidos TEXT NOT NULL, dni TEXT NOT NULL UNIQUE, " +
                "edad INTEGER, telefono TEXT, categoriaLicencia TEXT NOT NULL);";

        String sqlPasajero = "CREATE TABLE IF NOT EXISTS pasajero (" +
                "idpasajero INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombres TEXT NOT NULL, apellidos TEXT NOT NULL, dni TEXT NOT NULL UNIQUE, " +
                "edad INTEGER, telefono TEXT, direccion TEXT, fechaNacimiento DATE);";

        String sqlVehiculo = "CREATE TABLE IF NOT EXISTS vehiculo (" +
                "idvehiculo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descripcion TEXT NOT NULL, marca TEXT NOT NULL, " +
                "capacidad INTEGER NOT NULL CHECK(capacidad > 0));";

        String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuario (" +
                "idusuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user TEXT UNIQUE, password TEXT, tipoUser TEXT, nombre TEXT, apellidos TEXT, dni TEXT);";

        // tarifario: destinos, horarios y precios establecidos
        String sqlTarifa = "CREATE TABLE IF NOT EXISTS tarifa (" +
                "idtarifa INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "origen TEXT NOT NULL, destino TEXT NOT NULL, " +
                "hora_salida TEXT NOT NULL, duracion_min INTEGER NOT NULL, precio REAL NOT NULL, " +
                "UNIQUE(origen, destino, hora_salida));";

        // fecha_salida y fecha_llegada guardan fecha y hora: yyyy-MM-dd HH:mm
        String sqlViaje = "CREATE TABLE IF NOT EXISTS viaje (" +
                "idviaje INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tarifaId INTEGER NOT NULL, conductorId INTEGER NOT NULL, vehiculoId INTEGER NOT NULL, " +
                "fecha_salida DATETIME NOT NULL, fecha_llegada DATETIME NOT NULL, " +
                "FOREIGN KEY(tarifaId) REFERENCES tarifa(idtarifa), " +
                "FOREIGN KEY(conductorId) REFERENCES conductor(idconductor), " +
                "FOREIGN KEY(vehiculoId) REFERENCES vehiculo(idvehiculo));";

        String sqlPasaje = "CREATE TABLE IF NOT EXISTS pasaje (" +
                "idpasaje INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codigo TEXT NOT NULL UNIQUE, " +
                "pasajeroId INTEGER NOT NULL, viajeId INTEGER NOT NULL, " +
                "fecha_emision DATETIME NOT NULL, costo REAL NOT NULL, usuarioId INTEGER, " +
                "FOREIGN KEY(pasajeroId) REFERENCES pasajero(idpasajero), " +
                "FOREIGN KEY(viajeId) REFERENCES viaje(idviaje), " +
                "FOREIGN KEY(usuarioId) REFERENCES usuario(idusuario));";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlConductor);
            stmt.execute(sqlPasajero);
            stmt.execute(sqlVehiculo);
            stmt.execute(sqlUsuario);
            stmt.execute(sqlTarifa);
            stmt.execute(sqlViaje);
            stmt.execute(sqlPasaje);

            // INSERTAR USUARIO ADMIN POR DEFECTO (Si la tabla esta vacia)
            // Esto es vital para que el alumno pueda loguearse tras instalar
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO usuario (user, password, tipoUser, nombre) " +
                        "VALUES ('admin', 'admin123', 'ADMIN', 'Administrador');");
                System.out.println("Usuario inicial 'admin' creado.");
            }

            // INSERTAR EL TARIFARIO POR DEFECTO (Si la tabla esta vacia)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM tarifa");
            if (rs.next() && rs.getInt(1) == 0) {
                insertTarifario();
                System.out.println("Tarifario inicial creado.");
            }

            // INSERTAR VEHICULOS DE LA EMPRESA (Si la tabla esta vacia)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM vehiculo");
            if (rs.next() && rs.getInt(1) == 0) {
                insertVehiculos();
                System.out.println("Vehiculos iniciales creados.");
            }

            // INSERTAR CONDUCTORES DE LA EMPRESA (Si la tabla esta vacia)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM conductor");
            if (rs.next() && rs.getInt(1) == 0) {
                insertConductores();
                System.out.println("Conductores iniciales creados.");
            }

        } catch (SQLException e) {
            System.out.println("Error al inicializar tablas: " + e.getMessage());
        }
    }

    // Tarifario referencial de la zona (San Martin). Precios en soles,
    // tomados como referencia de empresas como Turismo Selva y de portales de venta de pasajes.
    // origen, destino, duracion en minutos, precio y horarios de salida
    private static final Object[][] TARIFARIO = {
            {"Tarapoto",   "Moyobamba",       120, 25.0, "06:00,10:00,14:00,18:00"},
            {"Moyobamba",  "Tarapoto",        120, 25.0, "05:30,09:30,13:30,17:30"},
            {"Tarapoto",   "Juanjui",         210, 30.0, "05:00,12:00,17:00"},
            {"Juanjui",    "Tarapoto",        210, 30.0, "05:00,12:00,17:00"},
            {"Moyobamba",  "Juanjui",         360, 50.0, "07:00,15:00"},
            {"Juanjui",    "Moyobamba",       360, 50.0, "06:00,14:00"},
            {"Moyobamba",  "Rioja",            40,  6.0, "06:30,12:30,17:30"},
            {"Rioja",      "Moyobamba",        40,  6.0, "07:30,13:30,18:30"},
            {"Moyobamba",  "Nueva Cajamarca",  60, 10.0, "07:00,13:00"},
            {"Nueva Cajamarca", "Moyobamba",   60, 10.0, "08:30,14:30"},
            {"Rioja",      "Tarapoto",        140, 28.0, "06:00,14:00"},
            {"Tarapoto",   "Rioja",           140, 28.0, "08:00,16:00"},
            {"Tarapoto",   "Yurimaguas",      150, 25.0, "06:00,13:00"},
            {"Yurimaguas", "Tarapoto",        150, 25.0, "06:00,13:00"},
            {"Tarapoto",   "Tocache",         330, 60.0, "06:00,15:00"},
            {"Tocache",    "Tarapoto",        330, 60.0, "05:30,14:30"},
            {"Tarapoto",   "Lamas",            25,  4.0, "07:00,11:00,16:00"},
            {"Lamas",      "Tarapoto",         25,  4.0, "08:00,12:00,17:00"},
            {"Moyobamba",  "Soritor",          30,  4.0, "06:00,10:00,15:00"},
            {"Soritor",    "Moyobamba",        30,  4.0, "07:00,11:00,16:00"}
    };

    private static void insertTarifario() throws SQLException {
        String sql = "INSERT INTO tarifa(origen,destino,hora_salida,duracion_min,precio) VALUES(?,?,?,?,?);";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            for (Object[] ruta : TARIFARIO) {
                for (String hora : ((String) ruta[4]).split(",")) {
                    st.setString(1, (String) ruta[0]);
                    st.setString(2, (String) ruta[1]);
                    st.setString(3, hora);
                    st.setInt(4, (Integer) ruta[2]);
                    st.setDouble(5, (Double) ruta[3]);
                    st.executeUpdate();
                }
            }
        }
    }

    // Flota inicial de la empresa: descripcion (auto, combi, bus), marca y capacidad de personas
    private static final Object[][] VEHICULOS = {
            {"auto",  "Toyota",        4},
            {"auto",  "Hyundai",       4},
            {"auto",  "Nissan",        4},
            {"combi", "Toyota",       15},
            {"combi", "Hyundai",      12},
            {"combi", "Mercedes-Benz", 19},
            {"bus",   "Volvo",        44},
            {"bus",   "Mercedes-Benz", 40},
            {"bus",   "Scania",       46},
            {"bus",   "Volvo",        40}
    };

    private static void insertVehiculos() throws SQLException {
        String sql = "INSERT INTO vehiculo(descripcion,marca,capacidad) VALUES(?,?,?);";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            for (Object[] v : VEHICULOS) {
                st.setString(1, (String) v[0]);
                st.setString(2, (String) v[1]);
                st.setInt(3, (Integer) v[2]);
                st.executeUpdate();
            }
        }
    }

    // Conductores iniciales (datos de ejemplo): nombres, apellidos, dni, edad, telefono y categoria de licencia
    private static final Object[][] CONDUCTORES = {
            {"Carlos Alberto", "Ramirez Soto",    "40125678", 45, "942118305", "A-IIIa"},
            {"Jorge Luis",     "Vasquez Tuesta",  "41236789", 41, "955230417", "A-IIIa"},
            {"Miguel Angel",   "Torres Pinedo",   "42347890", 38, "963341528", "A-IIIc"},
            {"Hector Manuel",  "Flores Saavedra", "43458901", 52, "971452639", "A-IIIc"},
            {"Walter",         "Delgado Cueva",   "44569012", 36, "982563740", "A-IIb"},
            {"Julio Cesar",    "Mori Rengifo",    "45670123", 33, "993674851", "A-IIb"},
            {"Edwin",          "Chavez Ruiz",     "46781234", 29, "944785962", "A-IIb"},
            {"Pedro Pablo",    "Salazar Medina",  "47892345", 48, "955896073", "A-IIIa"}
    };

    private static void insertConductores() throws SQLException {
        String sql = "INSERT INTO conductor(nombres,apellidos,dni,edad,telefono,categoriaLicencia) VALUES(?,?,?,?,?,?);";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            for (Object[] c : CONDUCTORES) {
                st.setString(1, (String) c[0]);
                st.setString(2, (String) c[1]);
                st.setString(3, (String) c[2]);
                st.setInt(4, (Integer) c[3]);
                st.setString(5, (String) c[4]);
                st.setString(6, (String) c[5]);
                st.executeUpdate();
            }
        }
    }

}
