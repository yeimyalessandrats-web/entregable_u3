module entregable_u3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    //requiero la dependencia de sqlite
    requires org.xerial.sqlitejdbc;
    requires javafx.base;
    requires javafx.graphics;

    opens entregable_u3 to javafx.fxml;

    opens entregable_u3.modelo to javafx.base, javafx.fxml;
    opens entregable_u3.controller to  javafx.fxml;
    opens entregable_u3.views to  javafx.fxml;

    exports entregable_u3;

    exports entregable_u3.utils;
    exports entregable_u3.test;
    exports entregable_u3.interfaces;
    exports entregable_u3.implementacion;
    exports entregable_u3.controller;

}
