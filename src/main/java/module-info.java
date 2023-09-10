module luca.raytracing {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;


    opens luca.raytracing to javafx.fxml;
    exports luca.raytracing;
}