module fr.isep.ye.projet_algo_jjx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires io;
    requires org.bouncycastle.util;

    opens fr.isep.ye.projet_algo_jjx to javafx.fxml;
    exports fr.isep.ye.projet_algo_jjx;
}