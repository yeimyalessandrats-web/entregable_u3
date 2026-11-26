package entregable_u3.test;

import entregable_u3.implementacion.ViajeDaoImpl;
import entregable_u3.interfaces.IViajeDao;
import entregable_u3.modelo.Conductor;
import entregable_u3.modelo.Tarifa;
import entregable_u3.modelo.Vehiculo;
import entregable_u3.modelo.Viaje;

import java.time.LocalDateTime;
import java.util.List;

// Requiere que ya existan la tarifa 1, el conductor 1 y el vehiculo 1
public class TestViaje {

    static IViajeDao Dao = new ViajeDaoImpl();

    public void insert(){
        Viaje v = new Viaje();
        Tarifa t = new Tarifa();
        Conductor c = new Conductor();
        Vehiculo ve = new Vehiculo();

        t.setIdtarifa(1);
        v.setTarifa(t);
        c.setIdconductor(1);
        v.setConductor(c);
        ve.setIdvehiculo(1);
        v.setVehiculo(ve);

        // fecha y hora de salida y de llegada
        v.setFechaSalida(LocalDateTime.parse("2026-12-01T06:00"));
        v.setFechaLlegada(LocalDateTime.parse("2026-12-01T08:00"));

        // validar que el conductor y el vehiculo no esten ocupados en esa fecha y hora
        if (Dao.validateConductor(v)){
            System.out.println("El conductor ya tiene un viaje en ese horario");
            return;
        }
        if (Dao.validateVehiculo(v)){
            System.out.println("El vehiculo ya tiene un viaje en ese horario");
            return;
        }
        boolean result = Dao.insert(v);
        if (result){
            System.out.println("Viaje success");
        }else{
            System.out.println("Viaje error");
        }
    }

    // Cambia el estado del viaje 1: E = En espera, T = En transito, D = En destino
    // y guarda la fecha y hora real de salida y de llegada
    public void cambiarEstado(){
        Viaje v = Dao.searchById(1);
        if (v == null){
            System.out.println("No existe el viaje 1");
            return;
        }
        // salida real: el viaje pasa a "En transito"
        v.setFechaSalidaReal(LocalDateTime.now().withSecond(0).withNano(0));
        v.setEstado("T");
        System.out.println(Dao.updateStatus(v) ? "En transito success" : "En transito error");

        // llegada real: el viaje pasa a "En destino"
        v.setFechaLlegadaReal(LocalDateTime.now().withSecond(0).withNano(0));
        v.setEstado("D");
        System.out.println(Dao.updateStatus(v) ? "En destino success" : "En destino error");
    }

    public void listar(){
        System.out.println("-----------------------------");
        List<Viaje> list = Dao.listAll();
        if (list !=null && !list.isEmpty()){
            for (Viaje v : list){
                System.out.println("ID_VIAJE:"+v.getIdviaje());
                System.out.println("ORIGEN:"+v.getTarifa().getOrigen());
                System.out.println("DESTINO:"+v.getTarifa().getDestino());
                System.out.println("SALIDA:"+v.getFechaSalida());
                System.out.println("LLEGADA:"+v.getFechaLlegada());
                System.out.println("SALIDA REAL:"+v.getFechaSalidaReal());
                System.out.println("LLEGADA REAL:"+v.getFechaLlegadaReal());
                System.out.println("ESTADO:"+v.getEstado());
                System.out.println("CONDUCTOR:"+v.getConductor().getNombres());
                System.out.println("VEHICULO:"+v.getVehiculo());
                System.out.println("PRECIO: S/."+v.getTarifa().getPrecio());
                System.out.println("-----------------------------");
            }
        }else{
            System.out.println("no hay registros");
        }
    }

    public static void main(String[] args) {
        TestViaje t = new TestViaje();
        // t.insert();
        // t.cambiarEstado();
        t.listar();
    }
}
