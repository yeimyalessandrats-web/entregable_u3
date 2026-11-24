module entregable_u2 {
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

    opens entregable_u2 to javafx.fxml;

    opens entregable_u2.modelo to javafx.base, javafx.fxml;
    opens entregable_u2.controller to  javafx.fxml;
    opens entregable_u2.views to  javafx.fxml;

    exports entregable_u2;

    exports entregable_u2.utils;
    exports entregable_u2.test;
    exports entregable_u2.interfaces;
    exports entregable_u2.implementacion;
    exports entregable_u2.controller;

}
