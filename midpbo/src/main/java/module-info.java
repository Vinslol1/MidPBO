module com.midpbo.fadjar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.midpbo.fadjar to javafx.fxml;
    exports com.midpbo.fadjar;
}
