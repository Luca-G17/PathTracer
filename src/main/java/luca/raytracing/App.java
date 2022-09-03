package luca.raytracing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    // RAYS:
    // Shoot rays out from each pixel location
    // Recursively track hits
    // If Hit: set colour of ray to material emittance
    // Generate new ray in random direction
    // BRDF = materialReflectance / PI
    // p = 1 / 2 * PI
    // return emittance + (BRDF * incomingColor * cos_theta / p)

    // OBJECTS:
    // each object needs a material which has an emittance and a reflectance prop


    @Override public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 1000);
        stage.setTitle("Ray Tracer");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}