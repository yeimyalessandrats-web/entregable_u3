package entregable_u3.test;

import entregable_u3.implementacion.TarifaDaoImpl;
import entregable_u3.interfaces.ITarifaDao;
import entregable_u3.modelo.Tarifa;

import java.time.LocalTime;
import java.util.List;

public class TestTarifa {
    static ITarifaDao Dao = new TarifaDaoImpl();

    public void insert(){
        Tarifa t = new Tarifa();
        t.setOrigen("Tarapoto");
        t.setDestino("Lamas");
        t.setHoraSalida(LocalTime.parse("07:30"));
        t.setDuracionMin(30);
        t.setPrecio(5.00);
        boolean result = Dao.insert(t);
        if (result){
            System.out.println("Tarifa success");
        }else{
            System.out.println("Tarifa error");
        }
    }

    public void listar(){
        System.out.println("-----------------------------");
        List<Tarifa> list = Dao.listAll();
        if (list !=null && !list.isEmpty()){
            for (Tarifa t : list){
                System.out.println("ID_TARIFA:"+t.getIdtarifa());
                System.out.println("ORIGEN:"+t.getOrigen());
                System.out.println("DESTINO:"+t.getDestino());
                System.out.println("HORA_SALIDA:"+t.getHoraSalida());
                System.out.println("DURACION(min):"+t.getDuracionMin());
                System.out.println("PRECIO: S/."+t.getPrecio());
                System.out.println("-----------------------------");
            }
        }else{
            System.out.println("no hay registros");
        }
    }

    public static void main(String[] args) {
        TestTarifa t = new TestTarifa();
        // t.insert();
        t.listar();
    }
}
