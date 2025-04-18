module com.midpbo.fadjar {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires transitive javafx.graphics;



    opens com.midpbo.fadjar to javafx.fxml;
    exports com.midpbo.fadjar;
}
