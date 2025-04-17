module com.midpbo.fadjar {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.midpbo.fadjar to javafx.fxml;
    exports com.midpbo.fadjar;
}
