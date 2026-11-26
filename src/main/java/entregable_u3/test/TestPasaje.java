package entregable_u3.test;

import entregable_u3.implementacion.PasajeDaoImpl;
import entregable_u3.implementacion.PasajeroDaoImpl;
import entregable_u3.implementacion.ViajeDaoImpl;
import entregable_u3.interfaces.IPasajeDao;
import entregable_u3.modelo.Pasaje;
import entregable_u3.modelo.Pasajero;
import entregable_u3.modelo.Usuario;
import entregable_u3.modelo.Viaje;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Requiere que ya existan el pasajero 1, el viaje 1 y el usuario admin (id 1)
public class TestPasaje {
    static IPasajeDao Dao = new PasajeDaoImpl();

    // compra de 2 pasajes para el viaje 1 (todos se guardan en una sola transaccion)
    public void insert(){
        Pasajero pa = new PasajeroDaoImpl().searchByDni("71234567");
        if (pa == null){
            System.out.println("No existe el pasajero");
            return;
        }
        // el comprador debe ser mayor de 18 años (en el sistema se valida en el formulario)
        if (!pa.esMayorDeEdad()){
            System.out.println("El pasajero es menor de edad, no puede comprar");
            return;
        }
        Usuario u = new Usuario();
        u.setIdusuario(1);
        Viaje v = new ViajeDaoImpl().searchById(1);
        LocalDateTime emision = LocalDateTime.now().withSecond(0).withNano(0);

        List<Pasaje> compra = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            Pasaje p = new Pasaje();
            p.setPasajero(pa);
            // el comprador es el que paga; los otros pasajeros se indicarian aqui con su propio pasajero
            p.setComprador(pa);
            p.setViaje(v);
            p.setCosto(25.00);
            p.setFechaEmision(emision);
            p.setUsuario(u);
            compra.add(p);
        }

        // validar que el pasajero no tenga otro viaje que se cruce en fecha y hora
        if (Dao.validateHorario(compra.get(0))){
            System.out.println("El pasajero ya tiene un viaje que se cruza con ese horario");
            return;
        }
        // validar que el pasajero no tenga ya un pasaje en ese mismo viaje
        if (Dao.validateAsiento(compra.get(0))){
            System.out.println("El pasajero ya tiene un pasaje para ese viaje");
            return;
        }
        // validar la capacidad de personas del vehiculo del viaje
        if (Dao.validatedCapacity(compra)){
            System.out.println("El viaje ya no tiene asientos disponibles");
            return;
        }
        if (Dao.insertAll(compra)){
            // los codigos se generan solos
            for (Pasaje p : compra){
                System.out.println("Pasaje success, codigo: "+p.getCodigo());
            }
        }else{
            System.out.println("Pasaje error");
        }
    }

    public void listar(){
        System.out.println("-----------------------------");
        List<Pasaje> list = Dao.listAll();
        if (list !=null && !list.isEmpty()){
            for (Pasaje p : list){
                System.out.println("CODIGO:"+p.getCodigo());
                System.out.println("EMISION:"+p.getFechaEmision());
                System.out.println("PASAJERO:"+p.getPasajero().getNombres()+" "+p.getPasajero().getApellidos());
                System.out.println("DNI:"+p.getPasajero().getDni());
                System.out.println("VIAJE:"+p.getViaje().getTarifa().getOrigen()+" -> "+p.getViaje().getTarifa().getDestino());
                System.out.println("SALIDA:"+p.getViaje().getFechaSalida());
                System.out.println("COSTO: S/."+p.getCosto());
                System.out.println("-----------------------------");
            }
        }else{
            System.out.println("no hay registros");
        }
    }

    // buscador por dni
    public void buscarPorDni(){
        List<Pasaje> list = Dao.searchByDni("71234567");
        System.out.println("Pasajes encontrados: "+list.size());
        for (Pasaje p : list){
            System.out.println(p.getCodigo()+" - "+p.getPasajero().getNombres());
        }
    }

    public static void main(String[] args) {
        TestPasaje t = new TestPasaje();
        // t.insert();
        t.listar();
        // t.buscarPorDni();
    }
}
