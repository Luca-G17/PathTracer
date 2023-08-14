package luca.raytracing;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.input.KeyEvent.KEY_TYPED;

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

    @Override public void start(Stage stage) throws IOException, InterruptedException {


        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MainWindow.fxml"));
        Controller controller = new Controller();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setTitle("Ray Tracer");
        stage.setScene(scene);
        stage.show();
        EventHandler<KeyEvent> keyEvent = keyEvent1 -> {
            if (keyEvent1.getCharacter().equals(" ")) {
                new Thread(() -> {
                    try {
                        final long startTime = System.currentTimeMillis();
                        controller.startTracing();
                        final long endTime = System.currentTimeMillis();
                        System.out.printf("Execution Time: %ds\n", (endTime - startTime) / 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        };

        stage.addEventHandler(KEY_TYPED, keyEvent);
    }
    public static void main(String[] args) {
        launch();
    }
}