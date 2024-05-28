module snps.lccn.dsn.dbexplorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.apache.logging.log4j;

    opens snps.lccn.dsn.dbexplorer to javafx.fxml;
    exports snps.lccn.dsn.dbexplorer;
}