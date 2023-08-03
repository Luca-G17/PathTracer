module luca.raytracing {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;


    opens luca.raytracing to javafx.fxml;
    exports luca.raytracing;
}